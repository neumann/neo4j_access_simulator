package applications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.partitioner.Partitioner;
import graph_gen_utils.partitioner.PartitionerAsRandom;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.LowNodecountPlacement;
import p_graph_service.policy.LowTrafficPlacement;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.Rnd;
import simulator.Simulator;
import simulator.tree.Distribution;
import simulator.tree.TreeLogIgnore_Factory;
import simulator.tree.TreeLogIgnore_Sim;
import simulator.tree.TreeLog_Sim;
import simulator.tree.TreeOps_Sim;
import simulator.tree.TreeOps_Sim.simType;

public class WriteOps_onTree {

	/**t	
	 * @param args
	 */
	public static void main(String[] args) {
		runWriteOps();
	}

	public static void runWriteOps() {
		PGraphDatabaseService db;
		Simulator sim;

		// size of the graph
		int nodesInGraph = 730027;
		int readRatio = 5;
		double[] changes = new double[5];

		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		byte[] seed = { 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16 };
		
		File source = new File("var/fstree-didic4_700kNodes_1300Relas_");
		Distribution dis = new Distribution(0.40, 0.40, 0.10, 0.10);
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/fstree-didic4_700kNodes_1300Relas_", 0, new RandomPlacement());
			sim = new TreeOps_Sim(db, "var/fstree-didic4_700kNodes_1300Relas_/ReadWrite_didic4_mix"+i, (int)Math.round(nodesInGraph*changes[i]*readRatio), simType.MIX, dis);
			Rnd.setSeed(seed);
			sim.startSIM();
			db.shutdown();
			
			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc +=changes[j];
			}
			File target = new File("results/fstree-didic4_700kNodes_1300Relas_randAdd"+perc);
			try {
				copyDirectory(source, target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// changing the seed
			for(int run = 0; run<seed.length ; run++){
				seed[run] += 3;
			}
		}
//		
//		File source = new File("var/fstree-didic4_700kNodes_1300Relas_trff");
//		for (int i = 0; i < changes.length; i++) {
//			db = new PGraphDatabaseServiceSIM(
//					"var/fstree-didic4_700kNodes_1300Relas_trff", 0,
//					new LowTrafficPlacement());
//			sim = new TreeLog_Sim(db, "var/fstree-didic4_700kNodes_1300Relas_trff/ReadWrite_didic4_mix_trffAdd"+i, "var/ReadWrite_didic4_mix"+i );
//			sim.startSIM();
//			db.shutdown();
//			
//			double perc = 0;
//			for (int j = 0; j <= i; j++) {
//				perc +=changes[j];
//			}
//			File target = new File("results/fstree-didic4_700kNodes_1300Relas_trffAdd"+perc);
//			try {
//				copyDirectory(source, target);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		
		source = new File("var/fstree-didic4_700kNodes_1300Relas_minN");
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/fstree-didic4_700kNodes_1300Relas_minN", 0,
					new LowNodecountPlacement());
			sim = new TreeLogIgnore_Sim(db, "var/fstree-didic4_700kNodes_1300Relas_minN/ReadWrite_didic4_mix_minAdd"+i, "var/ReadWrite_didic4_mix"+i );
			sim.startSIM();
			db.shutdown();
			
			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc +=changes[j];
			}
			File target = new File("results/fstree-didic4_700kNodes_1300Relas_minAdd"+perc);
			try {
				copyDirectory(source, target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
	// If targetLocation does not exist, it will be created.
	public static void copyDirectory(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]));
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
}
