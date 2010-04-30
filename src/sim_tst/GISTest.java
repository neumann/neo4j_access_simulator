package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;

public class GISTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		GraphDatabaseService db = new EmbeddedGraphDatabase(
				"var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		double addRatio = 0.25;
		double delRatio = 0.25;
		double localRatio = 0.25;
		double globalRatio = 0.25;
		long opCount = 20;
		OperationFactory operationFactory = new OperationFactoryGIS(db,
				addRatio, delRatio, localRatio, globalRatio, opCount);

		Simulator sim = new SimulatorGIS(db, "var/gis/log-gis-romania.txt",
				operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();
	}

}
