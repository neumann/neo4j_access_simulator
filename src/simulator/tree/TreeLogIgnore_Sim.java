package simulator.tree;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Simulator;

public class TreeLogIgnore_Sim extends Simulator {
	private TreeLogIgnore_Factory fac;
	private String inputLog;
	public TreeLogIgnore_Sim(GraphDatabaseService db, String logFile, String inputLog) {
		super(db, logFile);
		this.inputLog = inputLog;
	}

	@Override
	public void initiate() {
		fac = new TreeLogIgnore_Factory(inputLog);
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
