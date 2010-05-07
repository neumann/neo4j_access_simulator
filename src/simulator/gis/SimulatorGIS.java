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
		Rnd.setSeed(1000);
	}

	@Override
	public void loop() {
		if (operationFactory.hasNext()) {

			Operation op = operationFactory.next();

			long startTime = System.currentTimeMillis();

			System.out.printf("Operation[%d] Type[%s]...", op.getId(), op
					.getType());

			if (op.executeOn(getDB()) == false)
				System.out.printf("[!!FAILED!!]...");

			logOperation(op);

			System.out.printf("%s\n", getTimeStr(System.currentTimeMillis()
					- startTime));
		} else {
			getDB().shutdown();
			shutdown();
		}

	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
