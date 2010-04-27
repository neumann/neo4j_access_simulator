package simulator;

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

	public static long nextLong(long start, long end, RndType type) {
		double val = nextDouble(type);
		return Math.round(((val + start) * (end - start)));
	}

}
