package experiment;

import request_creators.*;
import request_transmitters.*;
import request_handlers.*;
import ioQueues.*;
import experiment.Controller_setter;
import java.io.FileWriter;
import java.io.PrintWriter;


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
			
			/*
			switch(setting.scenario) {
			case 1:
				System.out.println("Scenario One is chosen");
				handler = new RequestHandler(ioqueue, sqlTransmitter, setting.logIdentifier);
				break;
			case 2:
				System.out.println("Scenario 2 is chosen");
				handler = new PeriodicRequestHandler(ioqueue, sqlTransmitter, setting.logIdentifier);
				break;
			case 3:
				System.out.println("Scenario 3 is chosen");
				handler = new RequestHandler(ioqueue, sqlTransmitter, setting.logIdentifier);
				break;
				
			default:
				System.out.println("Scenario default is chosen");
				handler = new RequestHandler(ioqueue, sqlTransmitter, setting.logIdentifier);	
			}
			*/
			
			startTime = System.currentTimeMillis();
			
			creator.start();
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




