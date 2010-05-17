package sim_tst;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.Simulator;
import simulator.tree.ReadLogSim;
import simulator.tree.ReadOnlySim;
import simulator.tree.TreeInstSim;
import simulator.tree.TreeLog_Factory;

public class TreeTst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Simulator sim;
		PGraphDatabaseServiceSIM db;
		
		//test 1
		db = new PGraphDatabaseServiceSIM("var/RAND4_1400kNodes_2600kRelas", 0);
		sim = new ReadOnlySim(db, "rand4_10000_SearchRead.txt", 10000, 1);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/RAND2_1400kNodes_2600kRelas", 0);
		sim = new ReadOnlySim(db, "rand2_10000_SearchRead.txt", 10000, 1);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/RAND4_1400kNodes_2600kRelas", 0);
		sim = new ReadOnlySim(db, "rand4_10000_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		db = new PGraphDatabaseServiceSIM("var/RAND2_1400kNodes_2600kRelas", 0);
		sim = new ReadOnlySim(db, "rand2_10000_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		
		db = new PGraphDatabaseServiceSIM("var/RAND4_1400kNodes_2600kRelas", 0);
		sim = new TreeInstSim(db, "RAND4_10000_MIXWrite.txt", 10000);
		sim.startSIM();

		db = new PGraphDatabaseServiceSIM("var/RAND2_1400kNodes_2600kRelas", 0);
		sim = new TreeInstSim(db, "RAND2_10000_MIXWrite.txt", 10000);
		sim.startSIM();
		
		
//		TreeInstSim sim = new TreeInstSim(db, "instOut.txt");
//		ReadLogSim sim = new ReadLogSim(db, "instOut2.txt", "out.txt");
			
//		 convertDB();

	}

	public static void convertDB() {
		GraphDatabaseService db = new EmbeddedGraphDatabase("var/_1400kNodes_2600kRelas");
		Partitioner part = new PartitionerAsRandom((byte) 2);
		try {
			NeoFromFile.applyPtnToNeo(db, part);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 db.shutdown();
		 System.out.println("done");

		 PGraphDatabaseServiceSIM sim = new PGraphDatabaseServiceSIM("var/_1400kNodes_2600kRelas", 0);
		 sim.shutdown();
		
		 for(long id : sim.getInstancesIDs()){
			 System.out.println(sim.getInstanceInfoFor(id));
		 }
		
//		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
//		pDB.shutdown();
	}

}
