package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Simulator;

public class SimulatorGIS extends Simulator {
	private long count = 0;
	private OperationFactory operationFactory = null;

	public SimulatorGIS(GraphDatabaseService db, String logFile) {
		super(db, logFile);
	}

	public SimulatorGIS(GraphDatabaseService db, String logFile,
			OperationFactory operationFactory) {
		super(db, logFile);
		this.operationFactory = operationFactory;
	}

	@Override
	public void initiate() {
		Rnd.initiate(1000);
		count = 0;
	}

	@Override
	public void loop() {
		count++;

		if (operationFactory.hasNext()) {

			Operation op = operationFactory.next();

			System.out.printf("Operation[%d]: ID[%d] Type[%s]\n", count, op
					.getId(), op.getType());

			if (op.executeOn(getDB()) == false)
				System.out.println("\tFAILED!");

			logOperation(op);
		} else {
			getDB().shutdown();
			shutdown();
		}

	}

}
