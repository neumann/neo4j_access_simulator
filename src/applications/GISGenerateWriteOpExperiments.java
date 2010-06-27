package applications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.OperationFactoryGISConfig;
import simulator.gis.SimulatorGIS;

public class GISGenerateWriteOpExperiments {

	public static void main(String[] args) {

		// Thread t = new Thread();
		// System.out.printf("Thread Alive [Created] = %b\n", t.isAlive());
		// t.start();
		// System.out.printf("Thread Alive [Started] = %b\n", t.isAlive());
		// while (t.isAlive() == true) {
		// System.out.printf(".");
		// }
		// System.out.println();
		// System.out.printf("Thread Alive [After Poll] = %b\n", t.isAlive());
		// try {
		// t.join();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.printf("Thread Alive [Join Returned] = %b\n",
		// t.isAlive());

		// String targetDirName = String.format("%s/%s_%0,3.0f", "outputDir",
		// "sourceDir", 0.01 * 100);
		// System.out.println(targetDirName);

		// Params: InputDbPath OutputDbsPath InputLogsPath GraphNodeCount
		// E.g. var/gis-didic2 result_dbs/ logs-input/

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbPath:Str "
					+ "OutputDbsPath:Str " + "InputLogsPath:Str");
		}

		String inputDbDirStr = args[0];

		String outputDirStr = args[1];

		String inputLogsDirStr = args[2];

		start(inputDbDirStr, outputDirStr, inputLogsDirStr);
	}

	public static void start(String inputDbDirStr, String outputDirStr,
			String inputLogsDirStr) {

		PGraphDatabaseService db;
		OperationFactory operationFactory;
		Simulator sim;

		int readRatio = 5;

		double[] changes = new double[5];
		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		byte[][] seeds = new byte[5][16];
		seeds[0] = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
				15, 16 };
		seeds[1] = new byte[] { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 17 };
		seeds[2] = new byte[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 17, 18 };
		seeds[3] = new byte[] { 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
				17, 18, 19 };
		seeds[4] = new byte[] { 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
				18, 19, 20 };

		File inputLogsDir = new File(inputLogsDirStr);

		double ratioLong = 0.80 * 0.05; // 5% of all Read Ops are Long
		double ratioShort = 0.80 * 0.95; // 95% of all Read Ops are Long
		double ratioAdd = 0.00;
		double ratioDel = 0.00;
		double ratioShuffle = 0.20;

		for (int i = 0; i < changes.length; i++) {

			System.out.printf("Opening DB...");
			db = new PGraphDatabaseServiceSIM(inputDbDirStr, 0,
					new RandomPlacement());
			System.out.printf("Done\n");

			System.out.printf("Counting nodes in DB...");
			int nodesInGraph = 0;
			Transaction tx = db.beginTx();
			try {
				for (Node node : db.getAllNodes())
					nodesInGraph++;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				tx.finish();
			}
			System.out.printf("%d Nodes Found\n", nodesInGraph);

			int opCount = (int) Math.round(nodesInGraph * changes[i]
					* readRatio);

			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc += changes[j];
			}

			String logOutputPath = String.format("%s%s%0,3.0f", inputLogsDir
					.getAbsolutePath(), "/read_write_op_", perc * 100);

			OperationFactoryGISConfig config = new OperationFactoryGISConfig(
					ratioAdd, ratioDel, ratioShort, ratioLong, ratioShuffle,
					opCount, logOutputPath);

			operationFactory = new OperationFactoryGIS(db, config);

			System.out.printf("Simulation Details\n");
			System.out.printf("\tOperation Count = %d\n", opCount);
			System.out.printf("\tLog Output Path = %s\n", logOutputPath);

			sim = new SimulatorGIS(db, logOutputPath, operationFactory,
					seeds[i]);

			sim.startSIM();
			sim.shutdown();

			try {
				sim.join();
			} catch (InterruptedException e1) {
			}

			System.out.println("********************");
			System.out.println("Simulation Finished");
			System.out.println("********************");

			db.shutdown();

			String targetDirName = String.format("%s/%s_%0,3.0f", (new File(
					outputDirStr)).getAbsolutePath(), (new File(inputDbDirStr))
					.getName(), perc * 100);

			try {
				copyDirectory(new File(inputDbDirStr), new File(targetDirName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// If targetLocation does not exist, it will be created.
	private static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
}
