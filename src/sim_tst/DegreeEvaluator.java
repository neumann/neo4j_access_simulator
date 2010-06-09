package sim_tst;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.TreeSet;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.kernel.EmbeddedGraphDatabase;

public class DegreeEvaluator {

	public static void main(String[] args) {
		String dbDir = "/home/alex/workspace/graph_cluster_utils/sample dbs/simulated - gis romania/romania-gis-RAND4-GID-NAME-COORDS-BICYCLE";
		GraphDatabaseService db = new EmbeddedGraphDatabase(dbDir);
		DegreeEvaluator ev = new DegreeEvaluator(db);
		ev.calcDegree(true);
		try {

			File f = new File("/home/alex/Desktop/deg");
			ev.toFile(f);
		} catch (Exception e) {
			e.printStackTrace();
		}

		db.shutdown();

	}

	private GraphDatabaseService db;

	private HashMap<Integer, Long> inDegHist = new HashMap<Integer, Long>();
	private HashMap<Integer, Long> outDegHist = new HashMap<Integer, Long>();
	private HashMap<Integer, Long> degHist = new HashMap<Integer, Long>();

	private long nodeCount = 0;
	private double inDegSum = 0;
	private double outDegSum = 0;
	private double degSum = 0;

	int minInDegree = -1;
	int minOutDegree = -1;
	int minDegree = -1;

	int maxInDegree = -1;
	int maxOutDegree = -1;
	int maxDegree = -1;

	double avgOutDegree = -1;
	double avgInDegree = -1;
	double avgDegree = -1;

	double stdInDegree = -1;
	double stdOutDegree = -1;
	double stdDegree = -1;

	public DegreeEvaluator(GraphDatabaseService db) {
		this.db = db;
	}

	public void calcDegree(boolean histogram) {

		// initiate
		nodeCount = 0;
		inDegSum = 0;
		outDegSum = 0;
		degSum = 0;

		minInDegree = Integer.MAX_VALUE;
		minOutDegree = Integer.MAX_VALUE;
		minDegree = Integer.MAX_VALUE;

		maxInDegree = 0;
		maxOutDegree = 0;
		maxDegree = 0;

		if (histogram) {
			inDegHist.clear();
			outDegHist.clear();
			degHist.clear();
		}

		// calculate degree
		for (Node n : db.getAllNodes()) {
			nodeCount++;
			int deg = 0;
			int inDeg = 0;
			int outDeg = 0;
			for (Relationship rs : n.getRelationships(Direction.OUTGOING)) {
				outDeg++;
			}
			for (Relationship rs : n.getRelationships(Direction.INCOMING)) {
				inDeg++;
			}
			deg = inDeg + outDeg;

			if (histogram) {
				Long val = inDegHist.get(inDeg);
				if (val == null) {
					val = 1l;
				} else {
					val++;
				}
				inDegHist.put(inDeg, val);

				val = outDegHist.get(outDeg);
				if (val == null) {
					val = 1l;
				} else {
					val++;
				}
				outDegHist.put(outDeg, val);

				val = degHist.get(deg);
				if (val == null) {
					val = 1l;
				} else {
					val++;
				}
				degHist.put(deg, val);
			}

			inDegSum += inDeg;
			outDegSum += outDeg;
			degSum += deg;

			if (minDegree == -1) {
				minDegree = deg;
				minInDegree = inDeg;
				minOutDegree = outDeg;

				maxDegree = deg;
				maxInDegree = inDeg;
				maxOutDegree = outDeg;
			} else {

				if (deg < minDegree) {
					minDegree = deg;
				} else if (deg > maxDegree) {
					maxDegree = deg;
				}

				if (inDeg < minInDegree) {
					minInDegree = inDeg;
				} else if (deg > maxInDegree) {
					maxInDegree = inDeg;
				}

				if (outDeg < minOutDegree) {
					minOutDegree = outDeg;
				} else if (deg > maxOutDegree) {
					maxOutDegree = outDeg;
				}
			}
		}

		avgInDegree = inDegSum / (double) nodeCount;
		avgOutDegree = outDegSum / (double) nodeCount;
		avgDegree = degSum / (double) nodeCount;
	}

	public void calcDegreeSTD(boolean histogram) {
		if (avgDegree == -1) {
			calcDegree(histogram);
		}

		// calculate degree
		for (Node n : db.getAllNodes()) {
			int deg = 0;
			int inDeg = 0;
			int outDeg = 0;
			for (Relationship rs : n.getRelationships(Direction.OUTGOING)) {
				outDeg++;
			}
			for (Relationship rs : n.getRelationships(Direction.INCOMING)) {
				inDeg++;
			}
			deg = inDeg + outDeg;

			stdDegree += Math.pow((deg - avgDegree), 2);
			stdInDegree += Math.pow((inDeg - avgInDegree), 2);
			stdOutDegree += Math.pow((outDeg - avgOutDegree), 2);
		}
		stdDegree = stdDegree / (nodeCount - 1);
		stdInDegree = stdInDegree / (nodeCount - 1);
		stdOutDegree = stdOutDegree / (nodeCount - 1);

		stdDegree = Math.sqrt(stdDegree);
		stdInDegree = Math.sqrt(stdInDegree);
		stdOutDegree = Math.sqrt(stdOutDegree);
	}

	public void toFile(File f) throws FileNotFoundException {
		PrintStream ps = new PrintStream(f);
		ps.println("minInDegree = " + minInDegree);
		ps.println("maxInDegree = " + maxInDegree);
		ps.println("avgInDegree = " + avgInDegree);
		ps.println("stdInDegree = " + stdInDegree);
		ps.println();
		ps.println(inDegHist);
		ps.println();
		ps.println("minOutDegree = " + minOutDegree);
		ps.println("maxOutDegree = " + maxOutDegree);
		ps.println("avgOutDegree = " + avgOutDegree);
		ps.println("stdOutDegree = " + stdOutDegree);
		ps.println();
		ps.println(outDegHist);
		ps.println();
		ps.println("minDegree = " + minDegree);
		ps.println("maxDegree = " + maxDegree);
		ps.println("avgDegree = " + avgDegree);
		ps.println("stdDegree = " + stdDegree);
		ps.println();
		ps.println(degHist);
		ps.println();
		ps.close();

		File inHistFile = new File(f.getAbsoluteFile() + "inDegHist");
		ps = new PrintStream(inHistFile);
		printHist(inDegHist, ps);
		ps.close();

		File outHistFile = new File(f.getAbsoluteFile() + "outDegHist");
		ps = new PrintStream(outHistFile);
		printHist(outDegHist, ps);
		ps.close();

		File histFile = new File(f.getAbsoluteFile() + "degHist");
		ps = new PrintStream(histFile);
		printHist(degHist, ps);
		ps.close();
	}

	private void printHist(HashMap<Integer, Long> hist, PrintStream ps) {
		TreeSet<Integer> keys = new TreeSet<Integer>(hist.keySet());
		int cur = 0;
		for (int k : keys) {
			while (cur < k) {
				ps.println(0);
				cur++;
			}
			ps.println(hist.get(k));
			cur++;
		}
	}
}
