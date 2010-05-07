package sim_tst;

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

		// GraphDatabaseService db = new EmbeddedGraphDatabase(
		// "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		// GraphDatabaseService pdb = NeoFromFile
		// .writePNeoFromNeo(
		// "var/gis/partitioned-romania-BAL2-GID-NAME-COORDS-ALL_RELS",
		// db);

		String pdbDir = "/home/alex/workspace/neo4j_access_simulator/var/gis/";
		String pdbStr = "partitioned-romania-BAL2-GID-NAME-COORDS-ALL_RELS";
		PGraphDatabaseService pdb = new PGraphDatabaseServiceImpl(pdbDir
				+ pdbStr, 0);

		// double addRatio = 0.10;
		// double delRatio = 0.10;
		// double localRatio = 0.40;
		// double globalRatio = 0.40;
		// long opCount = 100;
		// OperationFactory operationFactory = new OperationFactoryGIS(db,
		// addRatio, delRatio, localRatio, globalRatio, opCount);

		String log = "/home/alex/Dropbox/Neo_Thesis_Private/log-gis-romania-100.txt";
		OperationFactory operationFactory = new LogOperationFactoryGIS(log);

		Simulator sim = new SimulatorGIS(pdb, "var/gis/log-gis-romania.txt",
				operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		pdb.shutdown();
	}

}
