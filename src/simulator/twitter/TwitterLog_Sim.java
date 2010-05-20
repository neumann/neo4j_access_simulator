package simulator.twitter;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class TwitterLog_Sim extends Simulator {
	private TwitterLog_Factory fac;
	private String inputLog;
	public TwitterLog_Sim(GraphDatabaseService db, String logFile, String inputLog) {
		super(db, logFile);
		this.inputLog = inputLog;
	}

	@Override
	public void initiate() {
		fac = new TwitterLog_Factory(inputLog);
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
