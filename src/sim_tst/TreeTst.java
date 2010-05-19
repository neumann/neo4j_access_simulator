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
import simulator.tree.TreeHardColor;
import simulator.tree.TreeInstSim;
import simulator.tree.TreeLog_Factory;

public class TreeTst {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GraphDatabaseService db;
		
		// hard color
		TreeHardColor.hardColor("target/Hard2_700kNodes_1300Relas", 2);
		db = new PGraphDatabaseServiceSIM("target/Hard2_700kNodes_1300Relas", 0);
		db.shutdown();
		
		System.out.println("done color hard 2");
		
		// hard color
		TreeHardColor.hardColor("target/Hard4_700kNodes_1300Relas", 4);
		db = new PGraphDatabaseServiceSIM("target/Hard4_700kNodes_1300Relas", 0);
		db.shutdown();
		
		System.out.println("done color hard 4");
		
		Simulator sim;
		//test 1
		db = new PGraphDatabaseServiceSIM("target/rand2_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "rand2__700kNodes_1300Relas_SearchRead.txt", 10000, 1);
		sim.startSIM();
		
		System.out.println("done search rand 2");
		
		db = new PGraphDatabaseServiceSIM("target/rand4_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "rand4_700kNodes_1300Relas_SearchRead.txt", 10000, 1);
		sim.startSIM();
		System.out.println("done search rand 4");
		
		
		db = new PGraphDatabaseServiceSIM("target/rand2_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "rand2__700kNodes_1300Relas_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		System.out.println("done count rand 2");
		
		
		db = new PGraphDatabaseServiceSIM("target/rand4_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "rand4_700kNodes_1300Relas_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		System.out.println("done count rand 4");
		
//		
		
		db = new PGraphDatabaseServiceSIM("target/Hard2_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "Hard2__700kNodes_1300Relas_SearchRead.txt", 10000, 1);
		sim.startSIM();
		
		System.out.println("done search hard 2");
		
		
		db = new PGraphDatabaseServiceSIM("target/Hard4_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "Hard4_700kNodes_1300Relas_SearchRead.txt", 10000, 1);
		sim.startSIM();
		
		System.out.println("done search hard 4");
		
		db = new PGraphDatabaseServiceSIM("target/Hard2_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "Hard2__700kNodes_1300Relas_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		System.out.println("done count hard 2");
		
		
		db = new PGraphDatabaseServiceSIM("target/Hard4_700kNodes_1300Relas", 0);
		sim = new ReadOnlySim(db, "Hard4_700kNodes_1300Relas_CountRead.txt", 10000, 0);
		sim.startSIM();
		
		System.out.println("done count hard 4");
		

//		TreeInstSim sim = new TreeInstSim(db, "instOut.txt");
//		ReadLogSim sim = new ReadLogSim(db, "instOut2.txt", "out.txt");
			
//		 convertDB();

	}

	public static void convertDB() {
		GraphDatabaseService db = new EmbeddedGraphDatabase("target/_700kNodes_1300Relas");
		Partitioner part = new PartitionerAsRandom((byte) 4);
		try {
			NeoFromFile.applyPtnToNeo(db, part);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 db.shutdown();
		 System.out.println("done");

		 PGraphDatabaseServiceSIM sim = new PGraphDatabaseServiceSIM("target/_700kNodes_1300Relas", 0);
		 sim.shutdown();
		
		 for(long id : sim.getInstancesIDs()){
			 System.out.println(sim.getInstanceInfoFor(id));
		 }
		
//		PGraphDatabaseService pDB = NeoFromFile.writePNeoFromNeo("var/pDB", db);
//		pDB.shutdown();
	}

}
