package applications_old;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.policy.RandomPlacement;
import p_graph_service.sim.PGraphDatabaseServiceSIM;
import simulator.Rnd;
import simulator.Simulator;
import simulator.SimulatorBasic;
import simulator.twitter.LogIgnoreOperationFactoryTwitter;

public class WriteIgnoreOps_onTwitter {

	/**
	 * t
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		runWriteOps();
	}

	public static void runWriteOps() {
		PGraphDatabaseService db;
		Simulator sim;

		// size of the graph
		double[] changes = new double[5];

		changes[0] = 0.01;
		changes[1] = 0.01;
		changes[2] = 0.03;
		changes[3] = 0.05;
		changes[4] = 0.15;

		byte[] seed = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };

		// read write distribution
		for (int i = 0; i < changes.length; i++) {
			db = new PGraphDatabaseServiceSIM(
					"var/twitter-256th-V[611643]E[852461]-didic4s-randAdd", 0,
					new RandomPlacement(seed[0]));
			db.setDBChangeLog("var/twitter-256th-V[611643]E[852461]-didic4s-randAdd/changelog"+i);
			LogIgnoreOperationFactoryTwitter fac = new LogIgnoreOperationFactoryTwitter("var/ReadWrite_didic4_mix"+i);
			sim = new SimulatorBasic(db, "var/twitter-256th-V[611643]E[852461]-didic4s-randAdd/ReadWrite_didic4_mix" + i, fac);
			Rnd.setSeed(seed);
			sim.startSIM();
			db.shutdown();
			
			// changing the seed
			for (int run = 0; run < seed.length; run++) {
				seed[run] += 3;
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
