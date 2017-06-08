package pidclcheck.enums;

import pidclcheck.dependencies.AccessDependency;
import pidclcheck.dependencies.AnnotateDependency;
import pidclcheck.dependencies.CreateDependency;
import pidclcheck.dependencies.DeclareDependency;
import pidclcheck.dependencies.Dependency;
import pidclcheck.dependencies.DeriveDependency;
import pidclcheck.dependencies.ExtendDependency;
import pidclcheck.dependencies.HandleDependency;
import pidclcheck.dependencies.ImplementDependency;
import pidclcheck.dependencies.ThrowDependency;

public enum DependencyType {
	ACCESS("access", AccessDependency.class), USEANNOTATION("useannotation", AnnotateDependency.class), CREATE("create",
			CreateDependency.class), DECLARE("declare", DeclareDependency.class), DERIVE("derive",
			DeriveDependency.class), EXTEND("extend", ExtendDependency.class), HANDLE("handle", HandleDependency.class), IMPLEMENT(
			"implement", ImplementDependency.class), THROW("throw", ThrowDependency.class), DEPEND("depend",
			Dependency.class);

	private final String value;
	private final Class<? extends Dependency> dependencyClass;

	private DependencyType(String value, Class<? extends Dependency> dependencyClass) {
		this.value = value;
		this.dependencyClass = dependencyClass;
	}

	public String getValue() {
		return this.value;
	}

	public Class<? extends Dependency> getDependencyClass() {
		return this.dependencyClass;
	}

	public final Dependency createGenericDependency(String classNameA, String classNameB) {
		if (this == ACCESS) {
			return new AccessDependency(classNameA, classNameB, null, null, null);
		} else if (this == USEANNOTATION) {
			return new AnnotateDependency(classNameA, classNameB, null, null, null);
		} else if (this == CREATE) {
			return new CreateDependency(classNameA, classNameB, null, null, null);
		} else if (this == DECLARE) {
			return new DeclareDependency(classNameA, classNameB, null, null, null);
		} else if (this == EXTEND) {
			return new ExtendDependency(classNameA, classNameB, null, null, null);
		} else if (this == IMPLEMENT) {
			return new ImplementDependency(classNameA, classNameB, null, null, null);
		} else if (this == THROW) {
			return new ThrowDependency(classNameA, classNameB, null, null, null, null);
		}
		return null;
	}
	
	public static final Dependency createGenericDependency(String classNameA, String type, String classNameB) {
		if (type.equals(ACCESS.getValue())) {
			return new AccessDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(USEANNOTATION.getValue())) {
			return new AnnotateDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(CREATE.getValue())) {
			return new CreateDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(DECLARE.getValue())) {
			return new DeclareDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(EXTEND.getValue())) {
			return new ExtendDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(IMPLEMENT.getValue())) {
			return new ImplementDependency(classNameA, classNameB, null, null, null);
		} else if (type.equals(THROW.getValue())) {
			return new ThrowDependency(classNameA, classNameB, null, null, null, null);
		}
		return null;
	}
}