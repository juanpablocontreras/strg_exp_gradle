package experiment;

import request_creators.*;
import request_transmitters.*;
import request_handlers.*;
import ioQueues.*;

import java.io.FileWriter;
import java.io.PrintWriter;

import control_setters.Controller_setter;
import global_enums.Creator_Distribution;

public class controller {
	

	public static void main(String[] args) throws Exception {
		
		//set up the experiment settings
		Controller_setter setting = new Controller_setter();
		
		
		// initiates the request creator, IO queue, request handler, and request transmitter
		SyncListIOQueue ioqueue = new SyncListIOQueue(setting.maxQueueSize);
		long startTime;
		long endTime;
		long total_time;
		String logFolderPath = "logs";
		
		System.out.println("controller started...");
		
		try {
			
			SqlRCreator creator = new SqlRCreator(ioqueue);
			Transmitter sqlTransmitter = new SqlRequestTransmitter();
			
			Thread handler = new ParentRequestHandler(ioqueue, sqlTransmitter, setting.logIdentifier);
			
			startTime = System.currentTimeMillis();
			
			creator.start();
			if(setting.creator_distribution.equals(Creator_Distribution.NONE)) {
				//wait for creator to finish putting all requests in queue
				creator.join();
			}
			
			handler.start();
			
			handler.join();
			endTime = System.currentTimeMillis();
			total_time = endTime - startTime;
			
			System.out.println("total execution time for " + setting.logIdentifier + ": " + total_time);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
	

	
}




