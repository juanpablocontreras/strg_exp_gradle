package request_types;

public class SqlRequest extends IORequest{
	

	public SqlRequest( 
			long size, 
			long id,
			String[] targetConnectionParams,
			String operation,
			OperationType optype,
			boolean isLastItem)
	{
		super(size, id, targetConnectionParams,operation,optype,isLastItem);
	}
}
