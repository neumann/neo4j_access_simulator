package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class TreeInstSim extends Simulator {
	private TreeOp_Factory fac;
	private int i;
	
	
	public TreeInstSim(GraphDatabaseService db, String logFile, int lenght) {
		super(db, logFile);
		this.i = lenght;	
	}

	@Override
	public void initiate() {
//		System.out.println("start inst");
		fac = new TreeOp_Factory(getDB(), i);
//		System.out.println("done inst");
	}

	@Override
	public void loop() {
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

}
