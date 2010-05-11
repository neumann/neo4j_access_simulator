package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class TreeInstSim extends Simulator {
	private TreeOp_Factory fac;
	private int i;
	private double addC;
	private double remC;
	
	
	public TreeInstSim(GraphDatabaseService db, String logFile, int lenght, double addC, double remC) {
		super(db, logFile);
		this.i = lenght;
		this.addC = addC;
		this.remC = remC;	
	}

	@Override
	public void initiate() {
//		System.out.println("start inst");
		fac = new TreeOp_Factory(getDB(), i,remC,addC);
//		System.out.println("done inst");
	}

	@Override
	public void loop() {
		if(fac.hasNext()){
			Operation op = fac.next();
			op.executeOn(getDB());
			logOperation(op);
		}else{
			getDB().shutdown();
			shutdown();
		}
	}

}
