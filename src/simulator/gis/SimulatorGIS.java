package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.Rnd;
import simulator.Simulator;

public class SimulatorGIS extends Simulator {
	private long count = 0;
	private LogOperationFactoryGIS logOperationFactory = null;

	public SimulatorGIS(GraphDatabaseService db, String logFile) {
		super(db, logFile);
		logOperationFactory = new LogOperationFactoryGIS(logFile);
	}

	@Override
	public void initiate() {
		Rnd.initiate(1000);
		count = 0;
	}

	@Override
	public void loop() {
		if (count == 100) {
			getDB().shutdown();
			this.shutdown();
		}
		count++;

		while (logOperationFactory.hasNext()) {
			Operation op = logOperationFactory.next();
			System.out.println(op.getId());
			System.out.println(op.getType());
			System.out.println("--------------------" + count);
			op.executeOn(getDB());
			logOperation(op);
		}

	}

}
