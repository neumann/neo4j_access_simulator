package sim_tst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISGenerateWriteOpExperiments {

	/**
	 * t
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Params: InputDbPath OutputDbsPath InputLogsPath GraphNodeCount
		// E.g. var/gis-didic2 result_dbs/ logs-input/ 785891

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbParth:Str "
					+ "OutputDbsPath:Str " + "InputLogsPath:Str "
					+ "GraphNodeCount:Int ");
		}

		String inputDbDirStr = args[0];
		String outputDirStr = args[1];
		String inputLogsDirStr = args[2];
		int nodesInGraph = Integer.parseInt(args[3]);

		PGraphDatabaseService db;
		OperationFactory operationFactory;
		Simulator sim;

		// size of the graph
		int readRatio = 5;
		double[] changes = new double[5];

		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		File sourceDbDir = new File(inputDbDirStr);
		File outputDir = new File(outputDirStr);
		File inputLogsDir = new File(inputLogsDirStr);

		for (int i = 0; i < changes.length; i++) {

			db = new PGraphDatabaseServiceSIM(inputDbDirStr, 0,
					new RandomPlacement());

			double ratioLong = 0.80 * 0.05; // 5% of all Read Ops are Long
			double ratioShort = 0.80 * 0.95; // 95% of all Read Ops are Long
			double ratioAdd = 0.00;
			double ratioDel = 0.00;
			double ratioShuffle = 0.20;
			int opCount = (int) Math.round(nodesInGraph * changes[i]
					* readRatio);

			operationFactory = new OperationFactoryGIS(db, ratioAdd, ratioDel,
					ratioShort, ratioLong, ratioShuffle, opCount);

			String logOutputPath = inputLogsDir.getAbsolutePath()
					+ "/read_write_op_" + i;

			sim = new SimulatorGIS(db, logOutputPath, operationFactory);

			sim.startSIM();

			db.shutdown();

			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc += changes[j];
			}

			File targetDbDir = new File(outputDir.getAbsolutePath() + "/"
					+ sourceDbDir.getName() + "_" + perc);

			try {
				copyDirectory(sourceDbDir, targetDbDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	// If targetLocation does not exist, it will be created.
	public static void copyDirectory(File sourceLocation, File targetLocation)
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
