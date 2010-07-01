package simulator;

import org.neo4j.graphdb.GraphDatabaseService;

public class BasicSimulator extends Simulator{
	private final OperationFactory fac;
	
	public BasicSimulator(GraphDatabaseService db, String logFile, OperationFactory fac) {
		super(db, logFile);
		this.fac = fac;
	}

	
	public void loop(){
		if(fac.hasNext()){
			Operation op = fac.next();
			op.executeOn(getDB());
			System.out.println(op.getId());
			logOperation(op);
		}else{
			getDB().shutdown();
			shutdown();
		}
	}


	@Override
	public void initiate() {
		// do nothing
	}

}
