package pidclcheck.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public final class DCLUtil {

	private DCLUtil() {
	}
	
	/**
	 * DCL2 Adjust the name of the class to make the identification easier It is
	 * done by converting all "/" to "."
	 * 
	 * Still "converts" the primitive types to your Wrapper.
	 * 
	 * @param className
	 *            Name of the class
	 * @return Adjusted class name
	 */
	public static String adjustClassName(String className) {
		if (className.startsWith("boolean") || className.startsWith("byte") || className.startsWith("short")
				|| className.startsWith("long") || className.startsWith("double") || className.startsWith("float")) {
			return "java.lang." + className.toUpperCase().substring(0, 1) + className.substring(1);
		} else if (className.startsWith("int")) {
			return "java.lang.Integer";
		} else if (className.startsWith("int[]")) {
			return "java.lang.Integer[]";
		} else if (className.startsWith("char")) {
			return "java.lang.Character";
		} else if (className.startsWith("char[]")) {
			return "java.lang.Character[]";
		}
		return className.replaceAll("/", ".");
	}



	/**
	 * DCL2 Method responsible to log error
	 * 
	 */
	public static String logError(Throwable thrownExeption) {
		if (thrownExeption == null) {
			throw new NullPointerException("thrownExeption cant be null");
		}

		final File logErrorFile = new File("dclcheck_"
				+ DateUtil.dateToStr(new Date(), "yyyyMMdd-HHmmss")
				+ "_error.log");

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		thrownExeption.printStackTrace(new PrintWriter(outputStream, true));
		try {
			outputStream.close();
		} catch (IOException e) {
			return null;
		}
		return logErrorFile.getName();
	}

	/**
	 * DCL2 Returns the module definition from the Java API
	 * 
	 * @return $java DCL constraint
	 */
	public static String getJavaModuleDefinition() {
		return "java.**,javax.**,org.ietf.jgss.**,org.omg.**,org.w3c.dom.**,org.xml.sax.**,boolean,char,short,byte,int,float,double,void";
	}

	/**
	 * DCL2 Checks if a className is contained in the Java API
	 * 
	 * @param className
	 *            Name of the class
	 * @return true if it is, no otherwise
	 */
	public static boolean isFromJavaAPI(final String className) {
		for (String javaModulePkg : getJavaModuleDefinition().split(",")) {
			String prefix = javaModulePkg.substring(0, javaModulePkg.indexOf(".**"));
			if (className.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	public static String getNumberWithExactDigits(int originalNumber, int numDigits) {
		String s = "" + originalNumber;
		while (s.length() < numDigits) {
			s = "0" + s;
		}
		return s;
	}


	/**
	 * Checks if a specific class is contained in a list of classes, RE or
	 * packages
	 */
	public static boolean hasClassNameByDescription(final String className, final String moduleDescription,
			final Map<String, String> modules, final Collection<String> projectClassNames) {
		for (String desc : moduleDescription.split(",")) {
			desc = desc.trim();

			if ("$system".equals(desc)) {
				/*
				 * If it's $system, any class
				 */
				return projectClassNames.contains(className);
			} else if (modules.containsKey(desc)) {
				/*
				 * If it's a module, call again the same method to return with
				 * its description
				 */
				if (hasClassNameByDescription(className, modules.get(desc), modules, projectClassNames)) {
					return true;
				}
			} else if (desc.endsWith("**")) {
				/* If it refers to any class in any package below one specific */
				desc = desc.substring(0, desc.length() - 2);
				if (className.startsWith(desc)) {
					return true;
				}
			} else if (desc.endsWith("*")) {
				/* If it refers to classes inside one specific package */
				desc = desc.substring(0, desc.length() - 1);
				if (className.startsWith(desc) && !className.substring(desc.length()).contains(".")) {
					return true;
				}
			} else if (desc.startsWith("\"") && desc.endsWith("\"")) {
				/* If it refers to regular expression */
				desc = desc.substring(1, desc.length() - 1);
				if (className.matches(desc)) {
					return true;
				}
			} else if (desc.endsWith("+")) {
				/*LIMITATION*/
				/*It is not implemented in pi-dclcheck*/
			} else {
				/* If it refers to a specific class */
				if (desc.equals(className)) {
					return true;
				}
			}
		}

		return false;
	}

	public static String getPackageFromClassName(final String className) {
		if (className.contains(".")) {
			return className.substring(0, className.lastIndexOf('.'));
		}
		return className;
	}

	public static String getSimpleClassName(final String qualifiedClassName) {
		return qualifiedClassName.substring(qualifiedClassName.lastIndexOf(".") + 1);
	}
	
}
