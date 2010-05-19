package simulator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.maths.random.ExponentialGenerator;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class Rnd {
	// private static long defaultSeed = 666;
	private static byte[] default_seed = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10, 11, 12, 13, 14, 15, 16 };
	private static Random rnd = null;
	private static ExponentialGenerator expoGen = null;
	private static double lambda = Math.E;
	private static HashMap<Double, ExponentialGenerator> expoGens = new HashMap<Double, ExponentialGenerator>();

	public static enum RndType {
		expo, unif, norm
	}

	private Rnd() {
	}

	public static void setSeed(byte[] seed) {
		default_seed = seed;
		rnd = null;
	}

	public static void setLambda(Double newLambda) {
		if (rnd == null) {
			// Fast & good randomness
			rnd = new MersenneTwisterRNG(default_seed);
		}
		if (expoGens.containsKey(newLambda) == false)
			expoGens.put(newLambda, new ExponentialGenerator(newLambda, rnd));
	}

	public static double nextDouble(RndType type) {
		return nextDouble(type, lambda);
	}

	public static double nextDouble(RndType type, Double lambda) {
		if (rnd == null) {
			// Fast & good randomness
			rnd = new MersenneTwisterRNG(default_seed);
		}

		switch (type) {
		case expo:
			// cdf for exponential distribution 1 − e^(− λx)
			// return 1 - Math.exp(-lambda * rnd.nextDouble());

			ExponentialGenerator expoGen = expoGens.get(lambda);
			if (expoGen == null) {
				expoGen = new ExponentialGenerator(lambda, rnd);
				expoGens.put(lambda, expoGen);
			}

			return expoGen.nextValue();
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
			rnd = new MersenneTwisterRNG(default_seed);
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
			rnd = new MersenneTwisterRNG(default_seed);
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
			rnd = new MersenneTwisterRNG(default_seed);
		}

		double val = nextDouble(type);
		return Math.round((val * (end - start)) + start);
	}

	public static long nextLongUnif(long start, long end) {
		if (rnd == null) {
			rnd = new MersenneTwisterRNG(default_seed);
		}

		double val = nextDouble(RndType.unif);
		return Math.round((val * (end - start)) + start);
	}

}
