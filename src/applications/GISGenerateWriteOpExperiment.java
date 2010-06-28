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

public class GISGenerateWriteOpExperiment {

	public static void main(String[] args) throws IOException {
		// Params: InputDbPath OutputDbPath LogPath PercentChurn
		// E.g. var/gis-didic2 result_dbs/gis-didic2_01
		// logs-input/read_write_op_01 0.01

		if (args[0].equals("help")) {
			System.out.println("Params - " + "InputDbPath:Str "
					+ "OutputDbPath:Str " + "LogPath:Str "
					+ " PercentChurn:Float");
		}

		String inputDbPathStr = args[0];

		String outputDbPathStr = args[1];

		String logPathStr = args[2];

		Double churn = Double.parseDouble(args[3]);

		start(inputDbPathStr, outputDbPathStr, logPathStr, churn);
	}

	public static void start(String inputDbPath, String outputDbPath,
			String logOutputPath, double churn) throws IOException {

		int readRatio = 5;

		double ratioLong = 0.80 * 0.05; // 5% of all Read Ops are Long
		double ratioShort = 0.80 * 0.95; // 95% of all Read Ops are Long
		double ratioAdd = 0.00;
		double ratioDel = 0.00;
		double ratioShuffle = 0.20;

		System.out.printf("Opening DB...");
		PGraphDatabaseService db = new PGraphDatabaseServiceSIM(inputDbPath, 0,
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

		int opCount = (int) Math.round(nodesInGraph * churn * readRatio);

		OperationFactoryGISConfig config = new OperationFactoryGISConfig(
				ratioAdd, ratioDel, ratioShort, ratioLong, ratioShuffle,
				opCount, logOutputPath);

		OperationFactory operationFactory = new OperationFactoryGIS(db, config);

		System.out.printf("Simulation Details\n");
		System.out.printf("\tOperation Count = %d\n", opCount);
		System.out.printf("\tLog Output Path = %s\n", logOutputPath);
		System.out.printf("\tDB Input Path = %s\n", inputDbPath);
		System.out.printf("\tDB Output Path = %s\n", outputDbPath);

		Simulator sim = new SimulatorGIS(db, logOutputPath, operationFactory);

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
