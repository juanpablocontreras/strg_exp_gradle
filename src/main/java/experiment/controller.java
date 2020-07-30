package experiment;

import request_creators.*;
import request_transmitters.*;
import request_handlers.*;
import ioQueues.*;
import java.io.FileWriter;
import java.io.PrintWriter;


public class controller {
	

	public static void main(String[] args) {
		
		//set up the experiment settings
		Settings setting = new Settings();
		setting.getSettings();
		
		// initiates the request creator, IO queue, request handler, and request transmitter
		SyncListIOQueue ioqueue = new SyncListIOQueue(setting.maxQueueCapacity);
		long startTime;
		long endTime;
		long total_time;
		String logFolderPath = "logs";
		
		System.out.println("controller started...");
		
		try {
			
			//Instantiate SQL request creator
			SqlRCreator creator = new SqlRCreator(
					ioqueue, 
					"EXP_ORIG",
					"EXP_TARGET",
					"juan",
					"LapinCoquin13",
					setting.databaseTableName);
			
			Transmitter sqlTransmitter = new SqlRequestTransmitter();
			RequestHandler handler = new RequestHandler(
					ioqueue, 
					setting.numberOfIOrequestsPerDataTransfer,
					sqlTransmitter);
			
			startTime = System.currentTimeMillis();
			
			creator.start();
			handler.start();
			
			handler.join();
			endTime = System.currentTimeMillis();
			total_time = endTime - startTime;
			
			//write time to file
			FileWriter write = new FileWriter(logFolderPath + "/total_time",false);
			PrintWriter pw = new PrintWriter(write);
			pw.print(total_time);
			pw.close();
			write.close();
			
			System.out.println("total execution time: " + total_time);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}




