package sim_tst;

import graph_gen_utils.NeoFromFile;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

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

		GraphDatabaseService db = new EmbeddedGraphDatabase(
				"var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		// GraphDatabaseService pdb = NeoFromFile
		// .writePNeoFromNeo(
		// "var/gis/partitioned-romania-BAL2-GID-NAME-COORDS-ALL_RELS",
		// db);

		double addRatio = 0.10;
		double delRatio = 0.10;
		double localRatio = 0.40;
		double globalRatio = 0.40;
		long opCount = 100;
		OperationFactory operationFactory = new OperationFactoryGIS(db,
				addRatio, delRatio, localRatio, globalRatio, opCount);
		// OperationFactory operationFactory = new LogOperationFactoryGIS(
		// "var/gis/log-gis-romania-input.txt");

		Simulator sim = new SimulatorGIS(db, "var/gis/log-gis-romania.txt",
				operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();
	}

}
