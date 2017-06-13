package pidclcheck.tests;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sun.xml.internal.bind.v2.model.util.ArrayInfoUtil;
import com.sun.xml.internal.ws.util.StringUtils;

import pidclcheck.core.DependencyConstraint.ArchitecturalDrift;
import pidclcheck.main.Main;
import sun.reflect.generics.tree.ArrayTypeSignature;

public class Test03 {

	private String constraintsFileContents; 
	private String dependenciesFileContents;
	
	@Before
	public void setUp() throws Exception {
		constraintsFileContents = "com.terra.pkg1.* cannot-handle com.terra.pkg1.B" + '\n' +
		"com.terra.pkg1.* must-create com.terra.pkg1.C" + '\n' +
		"only com.terra.pkg2.* can-useannotation com.terra.pkg2.D" + '\n' +
		"com.terra.pkg2.* can-throw-only java.lang.Exception" + '\n' +
		"com.terra.pkg1.* must-derive com.terra.pkg2.*" + '\n';
		
		dependenciesFileContents = "com.terra.pkg1.A,access,com.terra.pkg1.B" + '\n' +
		"com.terra.pkg1.A,access,java.lang.Math"+ '\n' +
		"com.terra.pkg1.A,declare,com.terra.pkg1.B"+ '\n' +
		"com.terra.pkg1.A,create,com.terra.pkg1.C"+ '\n' +
		"com.terra.pkg1.B,useannotation,com.terra.pkg2.D"+ '\n' +
		"com.terra.pkg1.B,extend,com.terra.pkg2.D"+ '\n' +
		"com.terra.pkg1.C,implement,com.terra.pkg2.E"+ '\n' +
		"com.terra.pkg2.E,throw,com.terra.pkg2.F"+ '\n';
	}

	@After
	public void tearDown() throws Exception {
		
		
		
	}

	@Test
	public void test() throws Exception{
		Collection<ArchitecturalDrift> violations = 
							Main.validateLocalArchitecture(
										new ByteArrayInputStream(dependenciesFileContents.getBytes()), 
										new ByteArrayInputStream(constraintsFileContents.getBytes())
							);
		
		
		Assert.assertEquals(6, violations.size());
		
		Collection<String> strViolations = new HashSet<String>();
		for (ArchitecturalDrift ad : violations){
			strViolations.add(ad.getInfoMessage() + ",[" + ad.getViolatedConstraint() + "]");
		}
		
		
		Assert.assertTrue(strViolations.contains("[divergence],[com.terra.pkg1.A,access,com.terra.pkg1.B],[com.terra.pkg1.* cannot-handle com.terra.pkg1.B]"));
		Assert.assertTrue(strViolations.contains("[divergence],[com.terra.pkg1.A,declare,com.terra.pkg1.B],[com.terra.pkg1.* cannot-handle com.terra.pkg1.B]"));
		Assert.assertTrue(strViolations.contains("[absence],[com.terra.pkg1.B,create,com.terra.pkg1.C],[com.terra.pkg1.* must-create com.terra.pkg1.C]"));
		Assert.assertTrue(strViolations.contains("[divergence],[com.terra.pkg1.B,useannotation,com.terra.pkg2.D],[only com.terra.pkg2.* can-useannotation com.terra.pkg2.D]"));
		Assert.assertTrue(strViolations.contains("[divergence],[com.terra.pkg2.E,throw,com.terra.pkg2.F],[com.terra.pkg2.* can-throw-only java.lang.Exception]"));
		Assert.assertTrue(strViolations.contains("[absence],[com.terra.pkg1.A,derive,com.terra.pkg2.*],[com.terra.pkg1.* must-derive com.terra.pkg2.*]"));
		
	}

}
