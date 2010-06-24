package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISGenerateOperations {

	public static void main(String[] args) {

		// Params: LogOutputPath DBDirectory AddRatio DelRatio ShortRatio
		// LongRatio OpCount
		// E.g. logs-output/log-gis-romania-SHORT_10000.txt var/ 0 0 1 0 0 10000

		if (args[0].equals("help")) {
			System.out.println("Params - " + "LogOutputPath:Str "
					+ "DBDirectory:Str " + "AddRatio:Double "
					+ "DelRatio:Double " + "ShortRatio:Double "
					+ "LongRatio:Double " + "ShuffleRatio:Double "
					+ "OpCount:Int");
		}

		String logOutputPath = args[0];

		String dbDir = args[1];

		Double addRatio = Double.parseDouble(args[2]);

		Double delRatio = Double.parseDouble(args[3]);

		Double shortRatio = Double.parseDouble(args[4]);

		Double longRatio = Double.parseDouble(args[5]);

		Double shuffleRatio = Double.parseDouble(args[6]);

		Long opCount = Long.parseLong(args[7]);

		// ****************

		// String logOutputPath =
		// "/home/alex/workspace/graph_cluster_utils/sample dbs/romania-gis-COORD-BAL_NS4-GID-NAME-COORDS-BICYCLE/gis-logs.txt";
		// String dbDir =
		// "/home/alex/workspace/graph_cluster_utils/sample dbs/romania-gis-COORD-BAL_NS4-GID-NAME-COORDS-BICYCLE/";
		// Double addRatio = 0d;
		// Double delRatio = 0d;
		// Double shortRatio = 0.8d;
		// Double longRatio = 0.2d;
		// Double shuffleRatio = 0d;
		// Long opCount = 500l;

		// ****************

		start(logOutputPath, dbDir, addRatio, delRatio, shortRatio, longRatio,
				shuffleRatio, opCount);
	}

	public static void start(String logOutputPath, String dbDir,
			Double addRatio, Double delRatio, Double shortRatio,
			Double longRatio, Double shuffleRatio, Long opCount) {

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		GraphDatabaseService db = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		start(logOutputPath, db, addRatio, delRatio, shortRatio, longRatio,
				shuffleRatio, opCount);

		db.shutdown();
	}

	public static void start(String logOutputPath, GraphDatabaseService db,
			Double addRatio, Double delRatio, Double shortRatio,
			Double longRatio, Double shuffleRatio, Long opCount) {

		OperationFactory operationFactory = new OperationFactoryGIS(db,
				addRatio, delRatio, shortRatio, longRatio, shuffleRatio,
				opCount);

		long startTime = System.currentTimeMillis();
		System.out.printf("SimulatorGIS From Generator...");

		Simulator sim = new SimulatorGIS(db, logOutputPath, operationFactory);
		sim.startSIM();

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));
	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
