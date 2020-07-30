package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Settings {
	
	public int maxQueueCapacity;
	public int numberOfIOrequestsPerDataTransfer;
	public String databaseTableName;
	
	
	public void getSettings() {
		try {
			File settingsFile = new File("exp_settings/exp_settings.txt");
			Scanner myReader = new Scanner(settingsFile);
			
			this.maxQueueCapacity = Integer.parseInt(myReader.nextLine());
			this.numberOfIOrequestsPerDataTransfer = Integer.parseInt(myReader.nextLine());
			this.databaseTableName = myReader.nextLine();
			
			myReader.close();
			
		}catch(FileNotFoundException e) {
			System.out.println(e);
		}
	}
	
}
