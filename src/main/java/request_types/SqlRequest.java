package request_types;

import global_enums.OperationType;

public class SqlRequest extends IORequest{
	

	public SqlRequest( 
			long size, 
			long id,
			String operation,
			OperationType optype,
			boolean isLastItem)
	{
		super(size, id, operation,optype,isLastItem);
	}
}
