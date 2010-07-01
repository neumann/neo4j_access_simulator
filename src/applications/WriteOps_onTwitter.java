package applications;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.LowNodecountPlacement;
import p_graph_service.policy.LowTrafficPlacement;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.BasicSimulator;
import simulator.Rnd;
import simulator.Simulator;
import simulator.twitter.TwitterLog_Factory;
import simulator.twitter.TwitterOps_Factory;

public class WriteOps_onTwitter {

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
		int nodesInGraph = 611643;
		int readRatio = 5;
		double[] changes = new double[5];

		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		byte[] seed = { 1, 2, 3, 4, 5, 6, 7, 8,
				9, 10, 11, 12, 13, 14, 15, 16 };
		
		File source = new File("var/twitter-256th-V[611643]E[852461]-didic4s-randAdd");
		//read write distribution
		double[] dis = {0.8,0.2};
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/twitter-256th-V[611643]E[852461]-didic4s-randAdd", 0, new RandomPlacement(seed[0]));
			TwitterOps_Factory fac = new TwitterOps_Factory((int)Math.round(nodesInGraph*changes[i]*readRatio), db, dis);
			sim = new BasicSimulator(db, "var/ReadWrite_didic4_mix"+i, fac);
			Rnd.setSeed(seed);
			sim.startSIM();
			
			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc +=changes[j];
			}
			File target = new File("results/twitter-256th-V[611643]E[852461]-didic4s-randAdd"+perc);
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
		source = new File("var/twitter-256th-V[611643]E[852461]-didic4s-trffAdd");
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/twitter-256th-V[611643]E[852461]-didic4s-trffAdd", 0,
					new LowTrafficPlacement());
			TwitterLog_Factory fac = new TwitterLog_Factory("var/ReadWrite_didic4_mix"+i);
			sim = new BasicSimulator(db, "var/twitter-256th-V[611643]E[852461]-didic4s-trffAdd/ReadWrite_didic4_mix_trffAdd"+i,  fac);
			sim.startSIM();
			
			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc +=changes[j];
			}
			File target = new File("results/twitter-256th-V[611643]E[852461]-didic4s-trffAdd"+perc);
			try {
				copyDirectory(source, target);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		source = new File("var/twitter-256th-V[611643]E[852461]-didic4s-minAdd");
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/twitter-256th-V[611643]E[852461]-didic4s-minAdd", 0,
					new LowNodecountPlacement());
			TwitterLog_Factory fac = new TwitterLog_Factory("var/ReadWrite_didic4_mix"+i);
			sim = new BasicSimulator(db, "var/twitter-256th-V[611643]E[852461]-didic4s-minAdd/ReadWrite_didic4_mix_minAdd"+i, fac );
			sim.startSIM();
			
			double perc = 0;
			for (int j = 0; j <= i; j++) {
				perc +=changes[j];
			}
			File target = new File("results/twitter-256th-V[611643]E[852461]-didic4s-minAdd"+perc);
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
