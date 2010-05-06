package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class TreeInstSim extends Simulator {
	private TreeOp_Factory fac;
	
	public TreeInstSim(GraphDatabaseService db, String logFile) {
		super(db, logFile);
	}

	@Override
	public void initiate() {
		System.out.println("start inst");
		fac = new TreeOp_Factory(getDB());
		System.out.println("done inst");
	}

	@Override
	public void loop() {
		if(fac.hasNext()){
			System.out.println("next step");
			Operation op = fac.next();
			op.executeOn(getDB());
			logOperation(op);
		}else{
			getDB().shutdown();
			shutdown();
		}
	}

}
