package request_transmitters;

import request_types.IORequest;

public abstract class Transmitter {
	
	public abstract void setUpConnection(String[] params) throws Exception;
	
	public abstract void performIORequest(IORequest request) throws Exception;
	
	public abstract void closeConnection() throws Exception;
	
}
