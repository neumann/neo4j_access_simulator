package sim_tst;

import java.util.Random;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.general.Consts;
import graph_gen_utils.general.DirUtils;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import p_graph_service.sim.InfoNode;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Simulator;
import simulator.Rnd.RndType;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;
import simulator.gis.astar.GISRelationshipTypes;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.ContinuousUniformGenerator;
import org.uncommons.maths.random.ExponentialGenerator;

public class GISTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String graphType = "NORMAL";
		// GraphDatabaseService db = new EmbeddedGraphDatabase(
		// "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		long startTime = System.currentTimeMillis();

		System.out.printf("Loading DB...");

		String graphType = "INFO";
		GraphDatabaseService db = new PGraphDatabaseServiceSIM(
				"var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS", 0);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis()
				- startTime));

		double addRatio = 0d;
		double delRatio = 0d;
		double localRatio = 1d;
		double globalRatio = 0d;
		long opCount = 100000;
		OperationFactory operationFactory = new OperationFactoryGIS(db,
				addRatio, delRatio, localRatio, globalRatio, opCount);

		// String inputLogDir = "var/gis/";
		// String inputLogFile = "log-gis-romania-INPUT-READ-GLOBAL-100.txt";
		//
		// OperationFactory operationFactory = new LogOperationFactoryGIS(
		// inputLogDir + inputLogFile);

		String outputLogDir = "var/gis/";
		String outputLogFile = String.format("log-gis-romania-OUTPUT-%s.txt",
				graphType);

		Simulator sim = new SimulatorGIS(db, outputLogDir + outputLogFile,
				operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();

		// GraphDatabaseService db = new EmbeddedGraphDatabase(
		// "/home/alex/workspace/neo4j_access_simulator/var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");
		//
		// try {
		// System.out.println(OperationFactoryGIS.getBucharestDiameter(db));
		// } finally {
		// db.shutdown();
		// }

	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
