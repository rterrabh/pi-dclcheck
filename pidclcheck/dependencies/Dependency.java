package pidclcheck.dependencies;

import java.io.Serializable;
import java.util.Properties;

import pidclcheck.enums.DependencyType;
import pidclcheck.enums.ViolationProperties;

public abstract class Dependency implements Serializable {
	protected final String classNameA;
	protected final String classNameB;
	protected final Integer lineNumberA;
	protected final Integer offset;
	protected final Integer length;

	protected Dependency(String classNameA, String classNameB, Integer lineNumberA, Integer offset, Integer length) {
		super();
		this.classNameA = classNameA;
		this.classNameB = classNameB;
		this.lineNumberA = lineNumberA;
		this.offset = offset;
		this.length = length;
	}

	public String getClassNameA() {
		return this.classNameA;
	}

	public String getClassNameB() {
		return this.classNameB;
	}

	public Integer getLineNumber() {
		return lineNumberA;
	}
	
	public Integer getOffset() {
		return this.offset;
	}
	
	public Integer getLength() {
		return this.length;
	}

	public Properties props() {
		Properties props = new Properties();
		props.put(ViolationProperties.CLASS_NAME_A.getKey(), this.classNameA);
		props.put(ViolationProperties.CLASS_NAME_B.getKey(), this.classNameB);
		props.put(ViolationProperties.LINE_NUMBER_A.getKey(), (this.lineNumberA != null) ? this.lineNumberA.toString()
				: "");
		props.put(ViolationProperties.AST_OFFSET.getKey(), (this.offset != null) ? this.offset.toString()
				: "");
		props.put(ViolationProperties.AST_LENGTH.getKey(), (this.length != null) ? this.length.toString()
				: "");
		return props;
	}

	public final boolean sameType(Dependency other) {
		return (this.getDependencyType().equals(other.getDependencyType()) && this.classNameB.equals(other.classNameB));
	}

	public abstract DependencyType getDependencyType();
	
	public String toShortString(){
			return "[divergence],[" + this.classNameA + "," + this.getDependencyType().getValue() + "," + this.classNameB + "]";
		
	}
}