package request_types;

public class IORequest {
	
	public long size;			//size of the data in the io request
	public long id;				//id of the IO request
	public boolean isLastItem;
	
	public long queueTimeArrival; //System time when arrived in Queue
	public long queueTimePolled; //System time when polled from queue
	
	public String[] targetConnectionParams; //parameters to set up a connection to the target system
	
	public String operation; //Operation or Query that will be executed by Transmitter (choose appropriate transmitter)
	public OperationType optype;
	
	public IORequest(
			long size, 
			long id,
			String[] targetConnectionParams, 
			String operation,
			OperationType optype,
			boolean isLastItem) 
	{
		this.size = size;
		this.id = id;
		this.targetConnectionParams = targetConnectionParams;
		this.operation = operation;
		this.optype = optype;
		this.isLastItem = isLastItem;
	}
	
}
