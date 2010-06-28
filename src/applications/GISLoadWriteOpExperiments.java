package applications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.LowNodecountPlacement;
import p_graph_service.policy.LowTrafficPlacement;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISLoadWriteOpExperiments {

	private enum InsertType {
		RANDOM, TRAFFIC, SIZE
	}

	public static void main(String[] args) throws Exception {

		// Params: InputDbPath OutputDbsPath InputLogsPath InsertType
		// E.g. var/gis-didic2 result_dbs/ logs-input/ random

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbPath:Str "
					+ "OutputDbsPath:Str " + "InputLogsPath:Str "
					+ "InsertType:Enum(random,traffic,size) ");
		}

		String inputDbDirStr = args[0];

		String outputDirStr = args[1];

		String inputLogsDirStr = args[2];

		String insertTypeStr = args[3];

		start(inputDbDirStr, outputDirStr, inputLogsDirStr, insertTypeStr);
	}

	public static void start(String inputDbDirStr, String outputDirStr,
			String inputLogsDirStr, String insertTypeStr) throws Exception {

		InsertType insertType = InsertType.RANDOM;
		if (insertTypeStr.equals("random") == true) {
			insertType = InsertType.RANDOM;
		} else if (insertTypeStr.equals("traffic") == true) {
			insertType = InsertType.TRAFFIC;
		} else if (insertTypeStr.equals("size") == true) {
			insertType = InsertType.SIZE;
		} else {
			throw new Exception("Invalid InsertType");
		}

		double[] changes = new double[5];

		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		for (int i = 0; i < changes.length; i++) {

			PGraphDatabaseService db = null;

			System.out.printf("Opening DB...");

			switch (insertType) {
			case RANDOM:
				db = new PGraphDatabaseServiceSIM(inputDbDirStr, 0,
						new RandomPlacement());
				break;
			case TRAFFIC:
				db = new PGraphDatabaseServiceSIM(inputDbDirStr, 0,
						new LowTrafficPlacement());
				break;
			case SIZE:
				db = new PGraphDatabaseServiceSIM(inputDbDirStr, 0,
						new LowNodecountPlacement());
				break;
			}

			if (db == null)
				throw new Exception(String
						.format("DB [%s] could not be created\n", insertType
								.toString()));

			System.out.printf("Done\n");

			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc += changes[j];
			}

			String logName = String.format("%s%0,3.0f", "read_write_op_",
					perc * 100);

			String logInputPath = (new File(inputLogsDirStr)).getAbsolutePath()
					+ "/" + logName;

			OperationFactory operationFactory = new LogOperationFactoryGIS(
					logInputPath);

			String logOutputPath = (new File(inputDbDirStr)).getAbsolutePath()
					+ "/" + logName;

			System.out.printf("Simulation Details\n");
			System.out.printf("\tInput Log Path = %s\n", logInputPath);
			System.out.printf("\tOutput Log Path = %s\n", logOutputPath);

			Simulator sim = new SimulatorGIS(db, logOutputPath,
					operationFactory);

			sim.startSIM();

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
