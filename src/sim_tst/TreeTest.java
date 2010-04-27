package sim_tst;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import simulator.Simulator;
import simulator.tree.RandomStaticSimulator;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

public class TreeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		PGraphDatabaseService pDB = new PGraphDatabaseServiceImpl("var/pDB", 0);
		Simulator sim = new RandomStaticSimulator(pDB, "log.txt");
		sim.startSIM();
		System.out.println("done");

		// convertDB();
	}

	public static void convertDB() {
		GraphDatabaseService db = new EmbeddedGraphDatabase("var/sampleDB");
		Partitioner part = new PartitionerAsRandom((byte) 2);
		try {
			NeoFromFile.applyPtnToNeo(db, part);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.shutdown();

		PGraphDatabaseService pDB = new PGraphDatabaseServiceImpl("var/pDB", 0);
		pDB.createDistribution("var/sampleDB");
		pDB.shutdown();
	}
}
