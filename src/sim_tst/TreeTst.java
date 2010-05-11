package sim_tst;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.tree.ReadLogSim;
import simulator.tree.ReadOnlySim;
import simulator.tree.TreeInstSim;
import simulator.tree.TreeLog_Factory;

public class TreeTst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
//		GraphDatabaseService db = new PGraphDatabaseServiceImpl("var/pDB", 0);
		
		PGraphDatabaseServiceSIM db = new PGraphDatabaseServiceSIM("var/randomInitSampleDB", 0);
		
//		TreeInstSim sim = new TreeInstSim(db, "instOut.txt");
//		ReadLogSim sim = new ReadLogSim(db, "instOut2.txt", "out.txt");
		ReadOnlySim sim = new ReadOnlySim(db, "out.txt", 10);
		
		sim.startSIM();
	
			
//		 convertDB();

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
		 System.out.println("done");

		 PGraphDatabaseServiceSIM sim = new PGraphDatabaseServiceSIM("var/sampleDB", 0);
		 sim.shutdown();
//		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
//		pDB.shutdown();
	}

}
