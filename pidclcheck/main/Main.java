package pidclcheck.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import pidclcheck.core.Architecture;
import pidclcheck.core.DependencyConstraint.ArchitecturalDrift;
import pidclcheck.exception.ParseException;

public class Main {

	public static void main(String[] args) {
		if (args.length != 3){
			System.out.println("Usage:\npi-dclcheck [dcl-file] [folder-dir] [dependencies-file]");
			return;
		}
		
		String constraintsFileName = args[0];
		String folderDirName = args[1];
		String dependenciesFileName = args[2];
		
		File dependenciesFile = new File(folderDirName + File.separator + dependenciesFileName);
		File constraintsFile = new File(folderDirName + File.separator + constraintsFileName);
		
		
		Collection<ArchitecturalDrift> violations;
		try {
			violations = validateLocalArchitecture(new FileInputStream(dependenciesFile), new FileInputStream(constraintsFile));
		} catch (ParseException e) {
			System.out.println("Problem when parsing. Cause: " + e.getMessage());
			return;
		} catch (IOException e) {
			System.out.println("Problem when reading file. Cause: " + e.getMessage());
			return;
		}
		
		try {
			File f = new File(folderDirName + File.separator + "violations.txt");
			FileWriter writer = new FileWriter(f);

			for (ArchitecturalDrift ad : violations){
				writer.write(ad.getInfoMessage() + ",[" + ad.getViolatedConstraint() + "]\n");
			}
			
			writer.close();			
		} catch (IOException e) {
			System.out.println("Problem when writing 'violations.txt' file. Cause: " + e.getMessage());
			return;
		}
		
	}

	/**
	 * Public method that receives the dependencies and the DCL constraints and returns the detected violations. 
	 * @param dependenciesIn Stream that has the content of the 'dependencies.txt' file
	 * @param constraintsIn Stream that has the content of the DCL constraints file.
	 * @return List of violations
	 * @throws ParseException When parsing of DCL constraints file fails. 
	 * @throws IOException When occurs IO errors.
	 * @author Ricardo Terra
	 */
	public static Collection<ArchitecturalDrift> validateLocalArchitecture(InputStream dependenciesIn, InputStream constraintsIn) throws ParseException, IOException {
		Architecture arch;
		
		arch = new Architecture(dependenciesIn, constraintsIn);			
		
		return arch.validate();
	}
	
}
