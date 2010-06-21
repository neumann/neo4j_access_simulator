package sim_tst;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.neo4j.graphdb.RelationshipType;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.LowNodecountPlacement;
import p_graph_service.policy.LowTrafficPlacement;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.SimulatorGIS;
import simulator.tree.TreeLogIgnore_Sim;

public class GISLoadWriteOpExperiments {

	private enum InsertType {
		RANDOM, TRAFFIC, SIZE
	}

	/**
	 * t
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// Params: InputDbPath OutputDbsPath InputLogsPath InsertType
		// E.g. var/gis-didic2 result_dbs/ logs-input/ random

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbParth:Str "
					+ "OutputDbsPath:Str " + "InputLogsPath:Str "
					+ "InsertType:Enum(random,traffic,size) ");
		}

		String inputDbDirStr = args[0];
		String outputDirStr = args[1];
		String inputLogsDirStr = args[2];
		String insertTypeStr = args[3];

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

		PGraphDatabaseService db;
		OperationFactory operationFactory;
		Simulator sim;

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
			db = null;

			switch (insertType) {
			case RANDOM:
				db = new PGraphDatabaseServiceSIM(
						sourceDbDir.getAbsolutePath(), 0, new RandomPlacement());
				break;
			case TRAFFIC:
				db = new PGraphDatabaseServiceSIM(
						sourceDbDir.getAbsolutePath(), 0,
						new LowTrafficPlacement());
				break;
			case SIZE:
				db = new PGraphDatabaseServiceSIM(
						sourceDbDir.getAbsolutePath(), 0,
						new LowNodecountPlacement());
				break;
			}

			String logName = "read_write_op_" + i;

			operationFactory = new LogOperationFactoryGIS(inputLogsDir
					.getAbsolutePath()
					+ "/" + logName);

			String logOutputPath = sourceDbDir.getAbsolutePath() + "/"
					+ logName;

			sim = new SimulatorGIS(db, logOutputPath, operationFactory);

			sim.startSIM();
			db.shutdown();

			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc += changes[j];
			}

			File target = new File(outputDir.getAbsolutePath() + "/"
					+ sourceDbDir.getName() + "_" + perc);

			try {
				copyDirectory(sourceDbDir, target);
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
