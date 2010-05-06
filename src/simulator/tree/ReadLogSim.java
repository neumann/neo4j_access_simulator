package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class ReadLogSim extends Simulator {
	private TreeLog_Factory fac;
	private String inputLog;
	public ReadLogSim(GraphDatabaseService db, String logFile, String inputLog) {
		super(db, logFile);
		this.inputLog = inputLog;
	}

	@Override
	public void initiate() {
		fac = new TreeLog_Factory(inputLog);
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
