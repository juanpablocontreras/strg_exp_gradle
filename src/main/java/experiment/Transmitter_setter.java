package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class Transmitter_setter {
	public String connectionStr;
	public String username;
	public String password;
	public String table_name;
	
	public Transmitter_setter() {
		getSettings();
	}
	
	
	public void getSettings() {
		try {
			File settingsFile = new File("exp_settings/trsm_settings.txt");
			Scanner myReader = new Scanner(settingsFile);
			
			this.connectionStr = myReader.nextLine();
			this.username = myReader.nextLine();
			this.password = myReader.nextLine();
			this.table_name = myReader.nextLine();
			
			myReader.close();
			
		}catch(FileNotFoundException e) {
			System.out.println("Transmitter setter error");
			System.out.println(e);
		}
	}
}
