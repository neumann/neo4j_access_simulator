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

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Params: LogInputPath LogOutputPath DBDirectory
		// E.g. logs-input/log-gis-romania-GLOBAL_500.txt
		// logs-output/log-gis-romania-GLOBAL_500_Results.txt var/

		if (args[0].equals("help")) {
			System.out.println("Params - " + "LogInputPath:Str "
					+ "LogOutputPath:Str " + "DBDirectory:Str ");
		}

		String logInputPath = args[0];

		String logOutputPath = args[1];

		String dbDir = args[2];

		// ****************

		// logInputPath = "var/gis/logs-input/log-gis-romania-GLOBAL_500.txt";
		// logOutputPath =
		// "var/gis/logs-output/log-gis-romania-GLOBAL_500_Results.txt";
		// dbDir = "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS";

		// ****************

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		GraphDatabaseService db = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		OperationFactory operationFactory = new LogOperationFactoryGIS(
				logInputPath);

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
