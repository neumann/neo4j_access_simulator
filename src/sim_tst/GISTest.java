package sim_tst;

import functionCallDB.FuncGraphDatabaseService;
import graph_gen_utils.NeoFromFile;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.PGraphDatabaseServiceImpl;

import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String graphType = "NORMAL";
		GraphDatabaseService db = new EmbeddedGraphDatabase(
				"var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		// String graphType = "INFO";
		// GraphDatabaseService db = new InfoGraphDatabaseService(
		// "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		// String graphType = "PARTITIONED";
		// String pdbDir =
		// "/home/alex/workspace/neo4j_access_simulator/var/gis/";
		// String pdbStr = "partitioned-romania-BAL2-GID-NAME-COORDS-ALL_RELS";
		// PGraphDatabaseService db = new PGraphDatabaseServiceImpl(pdbDir
		// + pdbStr, 0);

		// double addRatio = 0.0;
		// double delRatio = 0.0;
		// double localRatio = 0.25;
		// double globalRatio = 0.25;
		// long opCount = 1000;
		// OperationFactory operationFactory = new OperationFactoryGIS(db,
		// addRatio, delRatio, localRatio, globalRatio, opCount);

		String inputLog = "/home/alex/Dropbox/Neo_Thesis_Private/log-gis-romania.txt";
		OperationFactory operationFactory = new LogOperationFactoryGIS(inputLog);

		String outputLog = String.format("var/gis/log-gis-romania-%s.txt",
				graphType);

		Simulator sim = new SimulatorGIS(db, outputLog, operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();

		// System.out.println(InfoGraphDatabaseService.accessToString());
	}
}
