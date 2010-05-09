package sim_tst;

import functionCallDB.FuncGraphDatabaseService;
import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import simulator.tree.TreeInstSim;

public class TreeTst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
//		GraphDatabaseService db = new PGraphDatabaseServiceImpl("var/pDB", 0);
		
		FuncGraphDatabaseService db = new FuncGraphDatabaseService("var/sampleDB");
		
		TreeInstSim sim = new TreeInstSim(db, "instOut.txt");
//		ReadOnlySim sim = new ReadOnlySim(db, "out.txt");
		
		sim.startSIM();
		System.out.println(FuncGraphDatabaseService.accessToString());
				
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
		// db.shutdown();

		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
		pDB.shutdown();
	}

}
