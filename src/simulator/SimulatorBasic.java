package simulator;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Simulator;

public class SimulatorBasic extends Simulator {
	private OperationFactory operationFactory = null;
	private byte[] seed = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
			13, 14, 15, 16 };

	public SimulatorBasic(GraphDatabaseService db, String logFile,
			OperationFactory operationFactory) {
		super(db, logFile);
		this.operationFactory = operationFactory;
	}

	public SimulatorBasic(GraphDatabaseService db, String logFile,
			OperationFactory operationFactory, byte[] seed) {
		super(db, logFile);
		this.operationFactory = operationFactory;
		this.seed = seed;
	}

	@Override
	public void initiate() {
		Rnd.setSeed(seed);
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

			System.out.printf("%s", getTimeStr(System.currentTimeMillis()
					- startTime));
		} else {
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
