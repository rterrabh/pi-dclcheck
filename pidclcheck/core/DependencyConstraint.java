package pidclcheck.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pidclcheck.dependencies.Dependency;
import pidclcheck.enums.Constraint;
import pidclcheck.enums.ConstraintType;
import pidclcheck.util.DCLUtil;

public class DependencyConstraint implements Comparable<DependencyConstraint> {
	private final String moduleDescriptionA;
	private final String moduleDescriptionB;
	private final Constraint constraint;

	public DependencyConstraint(String moduleDescriptionA, String moduleDescriptionB, Constraint constraint) {
		super();
		this.moduleDescriptionA = moduleDescriptionA;
		this.moduleDescriptionB = moduleDescriptionB;
		this.constraint = constraint;
	}

	public List<ArchitecturalDrift> validate(String className, final Map<String, String> modules, Set<String> projectClasses,
		Collection<Dependency> dependencies) {
		switch (this.constraint.getConstraintType()) {
		case ONLY_CAN:
			if (DCLUtil.hasClassNameByDescription(className, moduleDescriptionA, modules, projectClasses)) {
				return null;
			}
			return this.validateCannot(className, moduleDescriptionB, this.constraint.getDependencyType().getDependencyClass(), modules,
					projectClasses, dependencies);

		case CANNOT:
			if (!DCLUtil.hasClassNameByDescription(className, moduleDescriptionA, modules, projectClasses)) {
				return null;
			}
			return this.validateCannot(className, moduleDescriptionB, this.constraint.getDependencyType().getDependencyClass(), modules,
					projectClasses, dependencies);

		case CAN_ONLY:
			if (!DCLUtil.hasClassNameByDescription(className, moduleDescriptionA, modules, projectClasses)) {
				return null;
			}
			return this.validateCanOnly(className, moduleDescriptionB, this.constraint.getDependencyType().getDependencyClass(), modules,
					projectClasses, dependencies);

		case MUST:
			if (!DCLUtil.hasClassNameByDescription(className, moduleDescriptionA, modules, projectClasses)) {
				return null;
			}
			return this.validateMust(className, moduleDescriptionB, this.constraint.getDependencyType().getDependencyClass(), modules,
					projectClasses, dependencies);
		}
		

		return null;
	}

	/**
	 * cannot
	 */
	private List<ArchitecturalDrift> validateCannot(String className, String moduleDescriptionB,
			Class<? extends Dependency> dependencyClass, Map<String, String> modules, Set<String> projectClasses,
			Collection<Dependency> dependencies) {
		List<ArchitecturalDrift> architecturalDrifts = new LinkedList<ArchitecturalDrift>();
		/* For each dependency */
		for (Dependency d : dependencies) {
			if (dependencyClass.isAssignableFrom(d.getClass())) {
				if (d.getClassNameB().equals(d.getClassNameA())) {
					continue;
				}
				/*LIMITATION*/
				/*It is not implemented in pi-dclcheck*/
				/* We disregard indirect dependencies to divergences */
				/*if (d instanceof ExtendIndirectDependency || d instanceof ImplementIndirectDependency){
					continue;
				}*/
				
				if (DCLUtil.hasClassNameByDescription(d.getClassNameB(), moduleDescriptionB, modules, projectClasses)) {
					architecturalDrifts.add(new DivergenceArchitecturalDrift(this, d));
				}
			}
		}
		return architecturalDrifts;
	}

	/**
	 * can only
	 */
	private List<ArchitecturalDrift> validateCanOnly(String className, String moduleDescriptionB,
			Class<? extends Dependency> dependencyClass, Map<String, String> modules, Set<String> projectClasses,
			Collection<Dependency> dependencies) {
		List<ArchitecturalDrift> architecturalDrifts = new LinkedList<ArchitecturalDrift>();

		/* For each dependency */
		for (Dependency d : dependencies) {
			if (dependencyClass.isAssignableFrom(d.getClass())) {
				if (d.getClassNameB().equals(d.getClassNameA())) {
					continue;
				}

				/*LIMITATION*/
				/*It is not implemented in pi-dclcheck*/
				/* We disregard indirect dependencies to divergences */
				/*if (d instanceof ExtendIndirectDependency || d instanceof ImplementIndirectDependency){
					continue;
				}*/
				if (!DCLUtil.hasClassNameByDescription(d.getClassNameB(), moduleDescriptionB, modules, projectClasses)) {
					architecturalDrifts.add(new DivergenceArchitecturalDrift(this, d));
				}

			}
		}
		return architecturalDrifts;
	}

	/**
	 * must
	 */
	private List<ArchitecturalDrift> validateMust(String className, String moduleDescriptionB, Class<? extends Dependency> dependencyClass,
			Map<String, String> modules, Set<String> projectClasses, Collection<Dependency> dependencies) {
		List<ArchitecturalDrift> architecturalDrifts = new LinkedList<ArchitecturalDrift>();

		// TODO: What am I supposed to do in case of internal class?
		if (className.contains("$")) {
			return null;
		} else if (className.equals(moduleDescriptionB)) {
			return null;
		} else if (DCLUtil.hasClassNameByDescription(className, moduleDescriptionB, modules, projectClasses)) {
			return null;
		}

		boolean found = false;
		for (Dependency d : dependencies) {
			if (dependencyClass.isAssignableFrom(d.getClass())) {
				if (DCLUtil.hasClassNameByDescription(d.getClassNameB(), moduleDescriptionB, modules, projectClasses)) {
					found = true;
					break;
				}
			}
		}
		if (!found) {
			architecturalDrifts.add(new AbsenceArchitecturalDrift(this, className, moduleDescriptionB));
		}

		return architecturalDrifts;
	}

	@Override
	public String toString() {
		return (this.constraint.getConstraintType().equals(ConstraintType.ONLY_CAN) ? "only " : "") + this.moduleDescriptionA + " "
				+ this.constraint.getValue() + " " + this.moduleDescriptionB;
	}

	public int compareTo(DependencyConstraint o) {
		return this.toString().compareTo(o.toString());
	}

	public Constraint getConstraint() {
		return this.constraint;
	}

	public String getModuleDescriptionA() {
		return this.moduleDescriptionA;
	}

	public String getModuleDescriptionB() {
		return this.moduleDescriptionB;
	}

	/**
	 * DCL2 Class that stores the crucial informations about the architectural
	 * drift
	 */
	public static abstract class ArchitecturalDrift {
		public static final String DIVERGENCE = "DIVERGENCE";
		public static final String ABSENCE = "ABSENCE";

		protected final DependencyConstraint violatedConstraint;

		protected ArchitecturalDrift(DependencyConstraint violatedConstraint) {
			super();
			this.violatedConstraint = violatedConstraint;
		}

		public final DependencyConstraint getViolatedConstraint() {
			return this.violatedConstraint;
		}

		public abstract String getDetailedMessage();

		public abstract String getInfoMessage();

		public abstract String getViolationType();

	}

	public static class DivergenceArchitecturalDrift extends ArchitecturalDrift {
		private final Dependency forbiddenDependency;

		public DivergenceArchitecturalDrift(DependencyConstraint violatedConstraint, Dependency forbiddenDependency) {
			super(violatedConstraint);
			this.forbiddenDependency = forbiddenDependency;
		}

		public final Dependency getForbiddenDependency() {
			return this.forbiddenDependency;
		}

		@Override
		public String getDetailedMessage() {
			return this.forbiddenDependency.toString();
		}

		@Override
		public String getInfoMessage() {
			return this.forbiddenDependency.toShortString();
		}

		@Override
		public String getViolationType() {
			return DIVERGENCE;
		}
	}

	public static class AbsenceArchitecturalDrift extends ArchitecturalDrift {
		private final String classNameA;
		private final String moduleDescriptionB;

		public AbsenceArchitecturalDrift(DependencyConstraint violatedConstraint, String classNameA, String moduleDescriptionB) {
			super(violatedConstraint);
			this.classNameA = classNameA;
			this.moduleDescriptionB = moduleDescriptionB;
		}

		public final String getClassNameA() {
			return this.classNameA;
		}

		public String getModuleNameB() {
			return this.moduleDescriptionB;
		}

		@Override
		public String getDetailedMessage() {
			return this.classNameA + " does not " + this.violatedConstraint.getConstraint().getDependencyType().getValue()
					+ " any type in " + this.violatedConstraint.getModuleDescriptionB();
		}

		@Override
		public String getInfoMessage() {
			return "[absence],[" + this.classNameA + "," + 
					this.violatedConstraint.getConstraint().getDependencyType().getValue() + "," + 
					this.moduleDescriptionB + "]";
		}

		@Override
		public String getViolationType() {
			return ABSENCE;
		}
	}

}
