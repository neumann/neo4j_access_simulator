package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;

import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Simulator;
import simulator.Rnd.RndType;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISLoadOperations {

	public static void main(String[] args) {

		// Params: LogInputPath LogOutputPath DBDirectory
		// E.g. logs-input/log-gis-romania-LONG_500.txt
		// logs-output/log-gis-romania-LONG_500_Results.txt var/

		if (args[0].equals("help")) {
			System.out.println("Params - " + "LogInputPath:Str "
					+ "LogOutputPath:Str " + "DBDirectory:Str ");
		}

		String logInputPath = args[0];

		String logOutputPath = args[1];

		String dbDir = args[2];

		// ****************

		// logInputPath = "var/gis/logs-input/log-gis-romania-LONG_500.txt";
		// logOutputPath =
		// "var/gis/logs-output/log-gis-romania-LONG_500_Results.txt";
		// dbDir = "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS";

		// ****************

		start(logInputPath, logOutputPath, dbDir);

	}

	public static void start(String logInputPath, String logOutputPath,
			String dbDir) {
		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		GraphDatabaseService db = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		start(logInputPath, logOutputPath, db);

		db.shutdown();
	}

	public static void start(String logInputPath, String logOutputPath,
			GraphDatabaseService db) {

		OperationFactory operationFactory = new LogOperationFactoryGIS(
				logInputPath);

		long startTime = System.currentTimeMillis();
		System.out.printf("SimulatorGIS From File...");

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
