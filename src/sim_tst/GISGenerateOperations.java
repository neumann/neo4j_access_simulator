package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISGenerateOperations {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Params: LogOutputPath DBDirectory AddRatio DelRatio ShortRatio
		// LongRatio OpCount
		// E.g. logs-output/log-gis-romania-SHORT_10000.txt var/ 0 0 1 0 10000

		if (args[0].equals("help")) {
			System.out.println("Params - " + "LogOutputPath:Str "
					+ "DBDirectory:Str " + "AddRatio:Double "
					+ "DelRatio:Double " + "ShortRatio:Double"
					+ "LongRatio:Double" + "OpCount:Int");
		}

		String logOutputPath = args[0];

		String dbDir = args[1];

		Double addRatio = Double.parseDouble(args[2]);

		Double delRatio = Double.parseDouble(args[3]);

		Double shortRatio = Double.parseDouble(args[4]);

		Double longRatio = Double.parseDouble(args[5]);

		Long opCount = Long.parseLong(args[6]);

		// ****************

		// logOutputPath =
		// "var/gis/logs-output/log-gis-romania-SHORT_10000.txt";
		// dbDir = "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS/";
		// addRatio = 0d;
		// delRatio = 0d;
		// shortRatio = 1d;
		// longRatio = 0d;
		// opCount = 500l;

		// ****************

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		GraphDatabaseService db = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		OperationFactory operationFactory = new OperationFactoryGIS(db,
				addRatio, delRatio, shortRatio, longRatio, opCount);

		Simulator sim = new SimulatorGIS(db, logOutputPath, operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();

	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
