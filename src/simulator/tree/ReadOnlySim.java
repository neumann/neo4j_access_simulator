package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class ReadOnlySim extends Simulator {
	ReadOnly_Factory fac;
	private int lenght;
	private double bal;
	public ReadOnlySim(GraphDatabaseService db, String logFile, int length,  double balance) {
		super(db, logFile);
		this.bal = balance;
		this.lenght = length;
	}

	@Override
	public void initiate() {
		fac = new ReadOnly_Factory(lenght, getDB(), bal);
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
