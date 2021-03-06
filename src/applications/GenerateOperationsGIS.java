package applications;

import jobs.SimJob;
import jobs.SimJobGenerateOpsGIS;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.gis.OperationFactoryGISConfig;

public class GenerateOperationsGIS {

	public static void main(String[] args) throws Exception {

		// Params: LogOutputPath DBDirectory AddRatio DelRatio ShortRatio
		// LongRatio OpCount
		// E.g. logs-output/log-gis-romania-SHORT_10000.txt var/ 0 0 1 0 0
		// 10000

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

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		PGraphDatabaseService pdb = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		OperationFactoryGISConfig config = new OperationFactoryGISConfig(
				addRatio, delRatio, shortRatio, longRatio, shuffleRatio,
				opCount, logOutputPath);

		SimJob job = new SimJobGenerateOpsGIS(
				new OperationFactoryGISConfig[] { config }, pdb);

		job.start();
		pdb.shutdown();
	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
