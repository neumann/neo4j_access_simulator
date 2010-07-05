package applications;

import jobs.SimJob;
import jobs.SimJobLoadOpsTwitter;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

public class LoadOperationsTwitter {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Params: LogInputPath LogOutputPath DBDirectory
		// E.g. logs-input/log-twitter-10000.txt logs-output/ var/

		if (args[0].equals("help")) {
			System.out.println("Params - " + "LogInputPath:Str "
					+ "LogOutputDirPath:Str " + "DBDirectory:Str ");
		}

		String logInputPath = args[0];

		String logOutputDirPath = args[1];

		String dbDir = args[2];

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		PGraphDatabaseService pdb = new PGraphDatabaseServiceSIM(dbDir, 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		String[] operationLogsIn = new String[] { logInputPath };

		SimJob job = new SimJobLoadOpsTwitter(operationLogsIn,
				logOutputDirPath, pdb, false);

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
