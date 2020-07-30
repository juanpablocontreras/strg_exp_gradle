package experiment;

import request_creators.*;
import request_transmitters.*;
import request_handlers.*;
import ioQueues.*;
import java.io.FileWriter;
import java.io.PrintWriter;


public class controller {

	
	
	public static void main(String[] args) {
		// initiates the request creator, IO queue, request handler, and request transmitter
		int ioQueueCapacity = 20;
		int numIORequestsPerDataTransfer = 20;
		SyncListIOQueue ioqueue = new SyncListIOQueue(ioQueueCapacity);
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
					"Small100");
			
			Transmitter sqlTransmitter = new SqlRequestTransmitter();
			RequestHandler handler = new RequestHandler(
					ioqueue, 
					numIORequestsPerDataTransfer,
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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}




