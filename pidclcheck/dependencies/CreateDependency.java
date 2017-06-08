package pidclcheck.dependencies;

import pidclcheck.enums.DependencyType;

public class CreateDependency extends Dependency {
	public CreateDependency(String classNameA, String classNameB, Integer lineNumberA, Integer offset, Integer length) {
		super(classNameA, classNameB, lineNumberA, offset, length);
	}

	@Override
	public DependencyType getDependencyType() {
		return DependencyType.CREATE;
	}

}