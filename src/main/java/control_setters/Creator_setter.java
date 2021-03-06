package control_setters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

//import global_enums.Creator_Distribution;


public class Creator_setter {
	public String connectionStr;
	public String username;
	public String password;
	public String table_name;
	public int total_number_of_items;
	public long period;
	//public Creator_Distribution distribution;
	
	
	public Creator_setter() {
		getSettings();
	}
	
	public void getSettings() {
		try {
			File settingsFile = new File("exp_settings/creator_settings.txt");
			Scanner myReader = new Scanner(settingsFile);
			
			this.connectionStr = myReader.nextLine();
			this.username = myReader.nextLine();
			this.password = myReader.nextLine();
			this.table_name = myReader.nextLine();
			this.total_number_of_items = Integer.parseInt(myReader.nextLine());
			this.period = Long.parseLong(myReader.nextLine());
			//this.distribution = Creator_Distribution.valueOf(myReader.nextLine());
			
			myReader.close();
			
		}catch(FileNotFoundException e) {
			System.out.println("Creator setter error");
			System.out.println(e);
		}
	}
	
}
