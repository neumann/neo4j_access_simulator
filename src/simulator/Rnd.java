package simulator;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class Rnd {
	private static long defaultSeed = 99999;
	private static Random rnd = null;

	public static enum RndType {
		expo, unif, norm
	}

	private Rnd() {
	}

	public static void setSeed(long seed) {
		defaultSeed = seed;
		rnd = null;
	}

	public static double nextDouble(RndType type) {
		if (rnd == null) {
			rnd = new Random(defaultSeed);
		}

		switch (type) {
		case expo:
			// cdf for exponential distribution 1 − e^(− λx)
			return 1 - Math.exp(-rnd.nextDouble());
		case unif:
			return rnd.nextDouble();
		case norm:
			return rnd.nextGaussian();
		default:
			throw new Error("Not supported RndType");
		}
	}

	public static Object[] getSampleFromMap(Map<Object, Double> elements,
			double sumOfElements, int sampleSize, RndType rndType) {
		if (rnd == null) {
			rnd = new Random(defaultSeed);
		}

		Object[] res = new Object[sampleSize];
		double[] val = new double[sampleSize];
		for (int i = 0; i < val.length; i++) {
			val[i] = nextDouble(rndType) * sumOfElements;
			res[i] = null;
		}

		boolean done = false;
		Iterator<Object> iterOnMap = elements.keySet().iterator();
		while (iterOnMap.hasNext() && !done) {
			Object cur = iterOnMap.next();
			double curVal = elements.get(cur);
			done = true;
			for (int i = 0; i < val.length; i++) {
				if (res[i] == null) {
					val[i] -= curVal;
					if (val[i] <= 0) {
						res[i] = cur;
					} else {
						done = false;
					}
				}
			}
		}
		return res;
	}

	public static long[] getSampleFromDB(GraphDatabaseService db,
			Evaluator eval, double sumOfElements, int sampleSize,
			RndType rndType) {
		if (rnd == null) {
			rnd = new Random(defaultSeed);
		}

		long[] res = new long[sampleSize];
		double[] val = new double[sampleSize];
		for (int i = 0; i < val.length; i++) {
			val[i] = nextDouble(rndType) * sumOfElements;
			res[i] = -1;
		}

		boolean done = false;
		Transaction tx = db.beginTx();
		try {
			Iterator<Node> iterOnMap = db.getAllNodes().iterator();
			while (iterOnMap.hasNext() && !done) {
				Node cur = iterOnMap.next();
				double curVal = eval.evaluate(cur);
				done = true;
				for (int i = 0; i < val.length; i++) {
					if (res[i] == -1) {
						val[i] -= curVal;
						if (val[i] <= 0) {
							res[i] = cur.getId();
						} else {
							done = false;
						}
					}
				}
			}
			tx.success();
		} finally {
			tx.finish();
		}

		return res;
	}

	public static long nextLong(long start, long end, RndType type) {
		if (rnd == null) {
			rnd = new Random(defaultSeed);
		}

		double val = nextDouble(type);
		return Math.round((val * (end - start)) + start);
	}

}
