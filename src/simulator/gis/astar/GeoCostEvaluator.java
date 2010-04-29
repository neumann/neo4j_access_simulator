package simulator.gis.astar;

import graph_gen_utils.general.Consts;

import org.neo4j.graphalgo.shortestpath.EstimateEvaluator;
import org.neo4j.graphdb.Node;

public class GeoCostEvaluator implements EstimateEvaluator<Double> {
	private static final double RADIUS_EARTH = 6371 * 1000; // Meters

	public Double getCost(final Node node, final Node goal) {
		return (distance(node, goal));
	}

	public static Double distance(final Node point1, final Node point2) {
		return distance((Double) point1.getProperty(Consts.LATITUDE),
				(Double) point1.getProperty(Consts.LONGITUDE), (Double) point2
						.getProperty(Consts.LATITUDE), (Double) point2
						.getProperty(Consts.LONGITUDE));
	}

	public static double distance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		latitude1 = Math.toRadians(latitude1);
		longitude1 = Math.toRadians(longitude1);
		latitude2 = Math.toRadians(latitude2);
		longitude2 = Math.toRadians(longitude2);
		double cLa1 = Math.cos(latitude1);
		double x_A = RADIUS_EARTH * cLa1 * Math.cos(longitude1);
		double y_A = RADIUS_EARTH * cLa1 * Math.sin(longitude1);
		double z_A = RADIUS_EARTH * Math.sin(latitude1);
		double cLa2 = Math.cos(latitude2);
		double x_B = RADIUS_EARTH * cLa2 * Math.cos(longitude2);
		double y_B = RADIUS_EARTH * cLa2 * Math.sin(longitude2);
		double z_B = RADIUS_EARTH * Math.sin(latitude2);
		return Math.sqrt((x_A - x_B) * (x_A - x_B) + (y_A - y_B) * (y_A - y_B)
				+ (z_A - z_B) * (z_A - z_B));
	}
}
