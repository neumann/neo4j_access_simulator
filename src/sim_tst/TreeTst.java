package sim_tst;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import simulator.tree.TreeInstSim;

public class TreeTst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db = new PGraphDatabaseServiceImpl("var/pDB",0);
//		ReadOnlySim sim = new ReadOnlySim(db, "out.txt");
		TreeInstSim sim = new TreeInstSim(db, "instOut.txt");
		
		sim.startSIM();
//		
//		convertDB();

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
		//db.shutdown();

		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
		pDB.shutdown();
	}

}
