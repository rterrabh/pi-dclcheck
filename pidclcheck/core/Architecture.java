package pidclcheck.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import pidclcheck.core.DependencyConstraint.ArchitecturalDrift;
import pidclcheck.core.parser.DCLParser;
import pidclcheck.dependencies.Dependency;
import pidclcheck.enums.DependencyType;
import pidclcheck.exception.ParseException;
import pidclcheck.util.DCLUtil;

public class Architecture {
	private static final boolean DEBUG = false;

	/**
	 * String: class name Collection<Dependency>: Collection of established
	 * dependencies
	 */
	public Map<String, Collection<Dependency>> projectClasses = null;

	/**
	 * String: module name String: module description
	 */
	public Map<String, String> modules = null;

	/**
	 * Collection<DependencyConstraint>: Collection of dependency constraints
	 */
	public Collection<DependencyConstraint> dependencyConstraints = null;	
	
	public Architecture(final InputStream dependenciesIn, final InputStream constraintsIn) throws ParseException, IOException {
		if (DEBUG) {
			System.out.println("Time BEFORE generate architecture (without dependencies): " + new Date());
		}
		this.projectClasses = new HashMap<String, Collection<Dependency>>();
		this.modules = new ConcurrentHashMap<String, String>();

		
		this.initializeDependencies(dependenciesIn);
		this.initializeDependencyConstraints(constraintsIn);
		
		if (DEBUG) {
			System.out.println("Time AFTER generate architecture (without dependencies): " + new Date());
		}
	}
	

	private void initializeDependencies(final InputStream in) throws ParseException, FileNotFoundException, IOException {
		LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
		while (reader.ready()){
			String[] line = reader.readLine().split(",");
			String sourceClass = line[0];
			String type = line[1];
			String targetClass = line[2];
			if (!this.projectClasses.containsKey(sourceClass)){
				this.projectClasses.put(sourceClass, new ArrayList<Dependency>());	
			}
			Dependency dep = DependencyType.createGenericDependency(sourceClass, type, targetClass);
			this.projectClasses.get(sourceClass).add(dep);
		}
		reader.close();
	}
	
	
	private void initializeDependencyConstraints(final InputStream in) throws ParseException, FileNotFoundException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		org.apache.commons.io.IOUtils.copy(in, baos);
		byte[] bytes = baos.toByteArray();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		
		this.modules.putAll(DCLParser.parseModules(bais));
		
		/* Define implicit modules */
		this.modules.put("$java", DCLUtil.getJavaModuleDefinition());
		/*
		 * Module $system has its behavior in
		 * DCLUtil.hasClassNameByDescription
		 */
		bais.reset();
		this.dependencyConstraints = DCLParser.parseDependencyConstraints(bais);
		
	}

	public Set<String> getProjectClasses() {
		return projectClasses.keySet();
	}

	public Collection<Dependency> getDependencies(String className) {
		return projectClasses.get(className);
	}

	public Dependency getDependency(String classNameA, String classNameB, Integer lineNumberA, DependencyType dependencyType) {
		Collection<Dependency> dependencies = projectClasses.get(classNameA);
		for (Dependency d : dependencies) {
			if (lineNumberA == null) {

			}
			if ((lineNumberA == null) ? d.getLineNumber() == null : lineNumberA.equals(d.getLineNumber())
					&& d.getClassNameB().equals(classNameB) && d.getDependencyType().equals(dependencyType)) {
				return d;
			}
		}
		return null;
	}

	public Collection<DependencyConstraint> getDependencyConstraints() {
		return this.dependencyConstraints;
	}

	public Map<String, String> getModules() {
		return this.modules;
	}


	public Collection<ArchitecturalDrift> validate(){
		Collection<ArchitecturalDrift> result = new ArrayList<ArchitecturalDrift>();
		
		Set<String> classNames = this.projectClasses.keySet();
		
		for (String className : classNames){
			Collection<Dependency> myDeps = this.getDependencies(className);
			
			for (DependencyConstraint dc : this.dependencyConstraints) {
				Collection<ArchitecturalDrift> singleResult = dc.validate(className, this.modules,
						classNames, myDeps);
				if (singleResult != null && !singleResult.isEmpty()) {
					result.addAll(singleResult);
				}
			}
		}
		
		return result;
	}

	

	

	
	

}
