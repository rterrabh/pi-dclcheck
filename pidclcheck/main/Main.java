package pidclcheck.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
		
		String constraintsFile = args[0];
		String folderDir = args[1];
		String dependenciesFile = args[2];
		
		Architecture arch;
		try {
			arch = new Architecture(new File(folderDir + File.separator + dependenciesFile), 
					new File(folderDir + File.separator + constraintsFile));		
		} catch (ParseException e) {
			System.out.println("Problem when parsing. Cause: " + e.getMessage());
			return;
		} catch (IOException e) {
			System.out.println("Problem when reading file. Cause: " + e.getMessage());
			return;
		}		
		
		Collection<ArchitecturalDrift> violations = arch.validate();
		
		
		try {
			File f = new File(folderDir + File.separator + "violations.txt");
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
	
}
