package control_setters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Controller_setter {
	public int maxQueueSize;
	public String logIdentifier;
	
	
	public Controller_setter() {
		getSettings();
	}
	
	public void getSettings() {
		try {
			File settingsFile = new File("exp_settings/controller_settings.txt");
			Scanner myReader = new Scanner(settingsFile);
			
			this.maxQueueSize = Integer.parseInt(myReader.nextLine());
			this.logIdentifier = myReader.nextLine();
			
			myReader.close();
			
		}catch(FileNotFoundException e) {
			System.out.println("Controller setter error");
			System.out.println(e);
		}
	}
	
	
}
