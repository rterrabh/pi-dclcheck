package pidclcheck.tests;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pidclcheck.main.Main;

public class Test01 {

	private final String TEMP_FOLDER = "tmp"; 
	private final String CONSTRAINTS_FILE = "architecture.dcl";
	private final String DEPENDENCIES_FILE = "dependencies.txt";
	private final String RESULT_FILE = "violations.txt";
	
	@Before
	public void setUp() throws Exception {
		File folder = new File(TEMP_FOLDER);
		if (!folder.exists()){
			folder.mkdir();
		}
		File constraintsFile = new File(folder.getAbsolutePath() + File.separator + CONSTRAINTS_FILE);
		File dependenciesFile = new File(folder.getAbsolutePath() + File.separator + DEPENDENCIES_FILE);
		
		FileWriter constraintsWriter = new FileWriter(constraintsFile);
		constraintsWriter.write("$system cannot-depend $java" + '\n');
		constraintsWriter.close();
		
		FileWriter dependenciesWriter = new FileWriter(dependenciesFile);
		dependenciesWriter.write("com.terra.pkg1.A,access,com.terra.pkg1.B" + '\n');
		dependenciesWriter.write("com.terra.pkg1.A,access,java.lang.Math"+ '\n');
		dependenciesWriter.write("com.terra.pkg1.A,declare,com.terra.pkg1.B"+ '\n');
		dependenciesWriter.write("com.terra.pkg1.A,create,com.terra.pkg1.C"+ '\n');
		dependenciesWriter.write("com.terra.pkg1.B,useannotation,com.terra.pkg2.D"+ '\n');
		dependenciesWriter.write("com.terra.pkg1.B,extend,com.terra.pkg2.D"+ '\n');
		dependenciesWriter.write("com.terra.pkg1.C,implement,com.terra.pkg2.E"+ '\n');
		dependenciesWriter.write("com.terra.pkg2.E,throw,com.terra.pkg2.F"+ '\n');
		dependenciesWriter.close();
		
	}

	@After
	public void tearDown() throws Exception {
		File folder = new File(TEMP_FOLDER);
		if (folder.exists()){
			for (File f : folder.listFiles()){
				f.delete();
			}
			folder.delete();
		}
	}

	@Test
	public void test() throws Exception{
		File folder = new File(TEMP_FOLDER);
		
		Main.main(new String[]{CONSTRAINTS_FILE,folder.getAbsolutePath(),DEPENDENCIES_FILE});
		
		Collection<String> violations = new ArrayList<String>();
				
		LineNumberReader resultReader = new LineNumberReader(new FileReader(new File(folder.getAbsolutePath() + File.separator + RESULT_FILE)));
		while (resultReader.ready()){
			violations.add(resultReader.readLine());
		}
		resultReader.close();
		
		Assert.assertEquals(1, violations.size());
		
		Assert.assertTrue(violations.contains("[divergence],[com.terra.pkg1.A,access,java.lang.Math],[$system cannot-depend $java]"));
	}

}
