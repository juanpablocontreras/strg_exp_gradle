package request_handlers;

import global_enums.Handler_Max_Type;
import experiment.Handler_setter;
import request_types.*;
import request_transmitters.*;
import ioQueues.*;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;

public class RequestHandler extends Thread{
	
	//Fields
	protected SyncListIOQueue ioRequestQueue; //reference to the IORequestQueue
	protected int pollingTime = 100; //amount of time between unsuccessful polls
	protected int numAttempts = 10; //number of attempts to get item from queue before proceeding to the data transfer anyway
	protected Transmitter transmitter;
	protected boolean wasLastItemProcessed = false;
	protected Handler_setter settings;
	
	protected String logFolderPath = "logs";
	protected String logIdentifier;
	protected PrintWriter printQueueLog_line;
	protected PrintWriter printTrsmLog_line;
	
	public RequestHandler(	
			SyncListIOQueue requestQeue,
			Transmitter transmitter,
			String logIdentifier) throws Exception
	{
		if(requestQeue == null) {
			throw new Exception("constructor queue not initialized");
		}
		this.ioRequestQueue = requestQeue;
		this.transmitter = transmitter;
		this.logIdentifier = logIdentifier;
	}
	
	
	@Override
	public void run() {
		//Perform Data transfers until application is closed
		
		try {
			
			//get settings
			this.settings = new Handler_setter();
			this.settings.getSettings();
			
			//list to hold the IO requests for one data transfer
			ArrayList<IORequest> dataTransferIORequests;
			
			//set up first append_to_file to false so previous files are erased
			boolean append_to_file = false;
			
			while(!wasLastItemProcessed) {
				//Perform 1 data transfer
				
				//create or set up the log files and writers
				//setUpLogPrinters(append_to_file);
				//append_to_file = true; //set to true so that the second+ data transfers print logs appended to the end of the log files
				
				//GET all IO requests for the data transfer into the cached list
				dataTransferIORequests = getRequestsForTransfer();
				
				
				/*
				 * LOG QUEUE TIMES
				 //logQueueTimes(dataTransferIORequests); 
				 */

				//Perform the IO requests of the data transfer
				if(dataTransferIORequests != null && !dataTransferIORequests.isEmpty()) {
					/*
					 * LOG TRANSMITTER TIMES
					 //logTransmitterTimes(performDataTransferIORequests(dataTransferIORequests));
					 */
					
					performDataTransferIORequests(dataTransferIORequests);
				}else {
					//System.out.println("No Requests...");
					Thread.sleep(pollingTime);
				}
				
				/*
				 * PRINTERS FOR QUEUE AND TRANSMITTER LOGS
				 //closPrinters();
				 */
				
			}
			
		}catch(Exception e){
			System.out.println("Handler error: ");
			System.out.println(e);
			//throw e;
		}
		
		
	}
	
	private ArrayList<IORequest> getRequestsForTransfer() throws InterruptedException {
		
		ArrayList<IORequest> dataTransferIORequests = new ArrayList<IORequest>();
		IORequest tempRequest;
		
	
		//GET all IO requests for the data transfer into the cached list
		int dataTransSize = 0;
		while(dataTransSize < settings.max_type_num) {
			
			//try getting an item from the IO queue
			tempRequest = this.ioRequestQueue.poll();
			if(tempRequest == null) {
				int attempt = numAttempts;
				
				while(tempRequest == null) {
					Thread.sleep(pollingTime);
					
					if(attempt-- <= 0) {
						return dataTransferIORequests; //all attempts were made to get the item from queue
					}
				}
			}
			
			dataTransferIORequests.add(tempRequest); //add request to data transfer cached list
			
			//increment size in queue 
			//(either the number of io requests or the size in bytes of the items in the queue)
			switch(settings.max_type) {
			case NUM_IO_REQUESTS:
				dataTransSize++;
				
			case SIZE:
				System.out.println("tempRequest size: " + tempRequest.size);
				dataTransSize += tempRequest.size;
				System.out.println("current dt size: " + dataTransSize);
			}
			
			
			//Intra process time
			Thread.sleep(this.settings.inter_IO_processing_time);
		}
		
		return dataTransferIORequests;
			

		
		
	}
	
	
	private ArrayList<Long> performDataTransferIORequests(ArrayList<IORequest> dataTransferIORequests) throws Exception 
	{
		synchronized(dataTransferIORequests) {
			
			if(dataTransferIORequests != null && !dataTransferIORequests.isEmpty()) {
				
				//Set up transmitter time measurements
				ArrayList<Long> transmitterTimes = new ArrayList<Long>();
				
				//set up connection
				this.transmitter.setUpConnection();
				
				//transmit the data
				transmitterTimes.add(System.currentTimeMillis());
				for(IORequest request:dataTransferIORequests) {
					transmitter.performIORequest(request);
					transmitterTimes.add(System.currentTimeMillis());
					
					//check if last item was processed
					if(request.isLastItem) {
						this.wasLastItemProcessed = true;
						//System.out.println("All items processed. experiment finished");
					}
				}
				
				//close connection
				transmitter.closeConnection();
				
				return transmitterTimes;
			}
			
			throw new Exception("No data to transfer (dataTransferIORequests is null or empty)");
		}
	}
	
	private void setUpLogPrinters(boolean append_to_file) throws Exception {
		FileWriter writeQ = new FileWriter(this.logFolderPath + "/queuelog" + this.logIdentifier, append_to_file);
		FileWriter writeT = new FileWriter(this.logFolderPath + "/trsmlog" + this.logIdentifier, append_to_file);
		
		this.printQueueLog_line = new PrintWriter(writeQ);
		this.printTrsmLog_line = new PrintWriter(writeT);
	}
	
	private void logQueueTimes(ArrayList<IORequest> requests) {
		for(IORequest request:requests) {
			printQueueLog_line.print(request.queueTimePolled-request.queueTimeArrival);
			printQueueLog_line.print("\n");
		}
	}
	
	private void logTransmitterTimes(ArrayList<Long> transmitterTimes) {
		for(int i=1;i<transmitterTimes.size();i++) {
			printTrsmLog_line.print(transmitterTimes.get(i)-transmitterTimes.get(i-1));
			printTrsmLog_line.print("\n");
		}
	}
	
	private void closPrinters() {
		this.printQueueLog_line.close();
		this.printTrsmLog_line.close();
	}
}
