package experiment;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import global_enums.*;


public class Handler_setter {
	public int max_type_num;//either num io req per dt or max size before dt
	public int inter_IO_processing_time;
	public Handler_Max_Type max_type;
	public Outcome_Type outcome_type;
	
	
	public Handler_setter() {
		getSettings();
	}
	
	public void getSettings() {
		try {
			File settingsFile = new File("exp_settings/handler_settings.txt");
			Scanner myReader = new Scanner(settingsFile);
			
			this.max_type_num = Integer.parseInt(myReader.nextLine());
			this.inter_IO_processing_time = Integer.parseInt(myReader.nextLine());
			this.max_type = Handler_Max_Type.valueOf(myReader.nextLine());
			this.outcome_type = Outcome_Type.valueOf(myReader.nextLine());
			
			myReader.close();
			
		}catch(FileNotFoundException e) {
			System.out.println("Handler setter error");
			System.out.println(e);
		}
	}
}
