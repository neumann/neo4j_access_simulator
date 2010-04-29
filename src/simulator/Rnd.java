package simulator;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class Rnd {
	public static enum RndType {
		expo, unif, norm
	}

	private static Random rnd = null;

	private Rnd() {
	}

	public static void initiate(long seed) {
		if (rnd == null) {
			rnd = new Random(seed);
		}
	}

	public static double nextDouble(RndType type) {
		if (rnd == null)
			throw new Error("Generator not initialized");

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

	public static Object[] getSample(Map<Object, Double> elements,
			double sumOfElements, int sampleSize, RndType rndType) {
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

	public static long nextLong(long start, long end, RndType type) {
		double val = nextDouble(type);
		return Math.round((val * (end - start)) + start);
	}

}
