package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class ReadOnlySim extends Simulator {
	ReadOnly_Factory fac;
	public ReadOnlySim(GraphDatabaseService db, String logFile) {
		super(db, logFile);
	}

	@Override
	public void initiate() {
		fac = new ReadOnly_Factory(10, getDB());
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
