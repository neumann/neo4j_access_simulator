package simulator.twitter;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class ReadOnlySim extends Simulator {
	ReadOnly_Factory fac;
	private int lenght;
	public ReadOnlySim(GraphDatabaseService db, String logFile, int length) {
		super(db, logFile);
		this.lenght = length;
	}

	@Override
	public void initiate() {
		fac = new ReadOnly_Factory(lenght, getDB());
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
