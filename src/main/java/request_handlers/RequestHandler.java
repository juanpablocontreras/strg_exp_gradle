package request_handlers;

import request_types.*;
import request_transmitters.*;
import ioQueues.*;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;

public class RequestHandler extends Thread{
	
	//Fields
	protected SyncListIOQueue ioRequestQueue; //reference to the IORequestQueue
	protected int numIOrequestsPerDataTransfer = 1;
	protected int interIOprocessTime = 100;
	protected int pollingTime = 100; //amount of time between unsuccessful polls
	protected int numAttempts = 10; //number of attempts to get item from queue before proceeding to the data transfer anyway
	protected Transmitter transmitter;
	protected boolean wasLastItemProcessed = false;
	
	protected String logFolderPath = "logs";
	protected PrintWriter printQueueLog_line;
	protected PrintWriter printTrsmLog_line;
	
	public RequestHandler(	
			SyncListIOQueue requestQeue,
			int numIOrequestsPerDataTransfer, 
			Transmitter transmitter) throws Exception
	{
		if(requestQeue == null) {
			throw new Exception("constructor queue not initialized");
		}
		this.ioRequestQueue = requestQeue;
		this.numIOrequestsPerDataTransfer = numIOrequestsPerDataTransfer;
		this.transmitter = transmitter;
	}
	
	
	@Override
	public void run() {
		//Perform Data transfers until application is closed
		
		try {
			//list to hold the IO requests for one data transfer
			ArrayList<IORequest> dataTransferIORequests;
			
			//set up first append_to_file to false so previous files are erased
			boolean append_to_file = false;
			
			while(!wasLastItemProcessed) {
				//Perform 1 data transfer
				
				//create or set up the log files and writers
				setUpLogPrinters(append_to_file);
				append_to_file = true; //set to true so that the second+ data transfers print logs appended to the end of the log files
				
				//GET all IO requests for the data transfer into the cached list
				dataTransferIORequests = getRequestsForTransfer();
				
				//log requests time spent in IO queue
				logQueueTimes(dataTransferIORequests); 
				
				//Perform the IO requests of the data transfer
				if(dataTransferIORequests != null && !dataTransferIORequests.isEmpty()) {
					logTransmitterTimes(performDataTransferIORequests(dataTransferIORequests));
				}else {
					System.out.println("No Requests...");
					Thread.sleep(pollingTime);
				}
				
				closPrinters();
			}
		}catch(Exception e){
			System.out.println(e);
		}
		
		
	}
	
	private ArrayList<IORequest> getRequestsForTransfer() throws InterruptedException {
		
		ArrayList<IORequest> dataTransferIORequests = new ArrayList<IORequest>();
		IORequest tempRequest;
		
		//GET all IO requests for the data transfer into the cached list
		int dataTransSize = this.numIOrequestsPerDataTransfer;
		while(dataTransSize > 0) {
			
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
			dataTransSize--;
			
			Thread.sleep(interIOprocessTime);
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
				IORequest temReq = dataTransferIORequests.get(0);
				String[] params = temReq.targetConnectionParams;
				this.transmitter.setUpConnection(params);
				
				//transmit the data
				transmitterTimes.add(System.currentTimeMillis());
				for(IORequest request:dataTransferIORequests) {
					transmitter.performIORequest(request);
					transmitterTimes.add(System.currentTimeMillis());
					
					//check if last item was processed
					if(request.isLastItem) {
						this.wasLastItemProcessed = true;
						closPrinters();
						System.out.println("All items processed. experiment finished");
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
		FileWriter writeQ = new FileWriter(this.logFolderPath + "/queuelog",append_to_file);
		FileWriter writeT = new FileWriter(this.logFolderPath + "/trsmlog",append_to_file);
		
		this.printQueueLog_line = new PrintWriter(writeQ);
		this.printTrsmLog_line = new PrintWriter(writeT);
	}
	
	private void logQueueTimes(ArrayList<IORequest> requests) {
		for(IORequest request:requests) {
			printQueueLog_line.print(request.queueTimePolled-request.queueTimeArrival);
			printQueueLog_line.print(",");
		}
	}
	
	private void logTransmitterTimes(ArrayList<Long> transmitterTimes) {
		for(int i=1;i<transmitterTimes.size();i++) {
			printTrsmLog_line.print(transmitterTimes.get(i)-transmitterTimes.get(i-1));
			printTrsmLog_line.print(",");
		}
	}
	
	private void closPrinters() {
		this.printQueueLog_line.close();
		this.printTrsmLog_line.close();
	}
}
