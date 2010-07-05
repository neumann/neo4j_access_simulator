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
import simulator.SimulatorBasic;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.operations.OperationGISAddNode;
import simulator.gis.operations.OperationGISDeleteNode;
import simulator.gis.operations.OperationGISShortestPathLong;
import simulator.gis.operations.OperationGISShortestPathShort;

public class ExperimentLoadWriteOpsGIS {

	private enum InsertType {
		RANDOM, TRAFFIC, SIZE
	}

	public static void main(String[] args) throws Exception {

		// Params: InputDbPath OutputDbPath InputLogPath InsertType
		// E.g. var/gis-didic2 result_dbs/gis-didic2_01
		// logs-input/read_write_op_01 logs-output/read_write_op_traffic_01
		// traffic

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbPath:Str "
					+ "OutputDbPath:Str " + "InputLogPath:Str "
					+ "OutputLogPath:Str "
					+ "InsertType:Enum(random,traffic,size) ");
		}

		String inputDbPath = args[0];

		String outputDbPath = args[1];

		String inputLogPath = args[2];

		String outputLogPath = args[3];

		String insertTypeStr = args[4];

		start(inputDbPath, outputDbPath, inputLogPath, outputLogPath,
				insertTypeStr);
	}

	public static void start(String inputDbPath, String outputDbPath,
			String inputLogPath, String outputLogPath, String insertTypeStr)
			throws Exception {

		String[] ignoreOps = null;

		InsertType insertType = InsertType.RANDOM;
		if (insertTypeStr.equals("random") == true) {
			insertType = InsertType.RANDOM;

			// Only do shuffle operations
			ignoreOps = new String[] { OperationGISAddNode.class.getName(),
					OperationGISDeleteNode.class.getName(),
					OperationGISShortestPathShort.class.getName(),
					OperationGISShortestPathLong.class.getName() };
			// ignoreOps = new String[] {};
		} else if (insertTypeStr.equals("traffic") == true) {
			insertType = InsertType.TRAFFIC;

			// Only do shuffle & read operations
			ignoreOps = new String[] { OperationGISAddNode.class.getName(),
					OperationGISDeleteNode.class.getName() };
			// ignoreOps = new String[] {};
		} else if (insertTypeStr.equals("size") == true) {
			insertType = InsertType.SIZE;

			// Only do shuffle operations
			ignoreOps = new String[] { OperationGISAddNode.class.getName(),
					OperationGISDeleteNode.class.getName(),
					OperationGISShortestPathShort.class.getName(),
					OperationGISShortestPathLong.class.getName() };
			// ignoreOps = new String[] {};
		} else {
			throw new Exception("Invalid InsertType");
		}

		PGraphDatabaseService db = null;

		System.out.printf("Opening DB...");

		switch (insertType) {
		case RANDOM:
			db = new PGraphDatabaseServiceSIM(inputDbPath, 0,
					new RandomPlacement());
			break;
		case TRAFFIC:
			db = new PGraphDatabaseServiceSIM(inputDbPath, 0,
					new LowTrafficPlacement());
			break;
		case SIZE:
			db = new PGraphDatabaseServiceSIM(inputDbPath, 0,
					new LowNodecountPlacement());
			break;
		}

		if (db == null)
			throw new Exception(String.format("DB [%s] could not be created\n",
					insertType.toString()));

		System.out.printf("Done\n");

		System.out.printf("Simulation Details\n");
		System.out.printf("\tInput Log Path = %s\n", inputLogPath);
		System.out.printf("\tOutput Log Path = %s\n", outputLogPath);

		OperationFactory operationFactory = new LogOperationFactoryGIS(
				inputLogPath, ignoreOps);

		Simulator sim = new SimulatorBasic(db, outputLogPath, operationFactory);

		sim.startSIM();

		System.out.println("********************");
		System.out.println("Simulation Finished");
		System.out.println("********************");

		db.shutdown();

		System.out.printf("Copying DB Snapshot...");
		copyDirectory(new File(inputDbPath), new File(outputDbPath));
		System.out.printf("Done\n");
		System.out.printf("From [%s] To [%s]", new File(inputDbPath)
				.getAbsoluteFile(), new File(outputDbPath).getAbsoluteFile());
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
