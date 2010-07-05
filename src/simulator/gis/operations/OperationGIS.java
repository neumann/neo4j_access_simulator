package simulator.gis.operations;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.gis.astar.Coordinates;

public abstract class OperationGIS extends Operation {

	protected static final String GIS_PATH_LENGTH_TAG = "pathlen";
	protected static final String GIS_DISTANCE_TAG = "distance";
	protected static final String GIS_PATH_TAG = "path";

	public OperationGIS(String[] args) {
		super(args);
		this.info.put(GIS_PATH_LENGTH_TAG, Long.toString(0));
		this.info.put(GIS_DISTANCE_TAG, Long.toString(0));
		this.info.put(GIS_PATH_TAG, new String());
	}

	@Override
	public abstract boolean onExecute(GraphDatabaseService db);

	// Top Romanian cities, largest-to-smallest
	public static double getMinDistanceToCityScore(double sourceLon,
			double sourceLat) {

		ArrayList<Coordinates> citiesCoords = new ArrayList<Coordinates>();

		// Romania [longitude=24.9804, latitude=45.946949]

		// Bucharest [longitude=26.102965, latitude=44.434295]
		citiesCoords.add(new Coordinates(44.434295, 26.102965));

		// Iasi [longitude=27.590505, latitude=47.160365]
		citiesCoords.add(new Coordinates(47.160365, 27.590505));

		// Galati [longitude=28.054665, latitude=45.433675]
		citiesCoords.add(new Coordinates(45.433675, 28.054665));

		// Timisoara [longitude=21.223305, latitude=45.75343]
		citiesCoords.add(new Coordinates(45.75343, 21.223305));

		// Constanta [longitude=28.65328, latitude=44.176975]
		citiesCoords.add(new Coordinates(44.176975, 28.65328));

		// // Cluj-Napoca [longitude=23.585135, latitude=46.768515]
		// citiesCoords.add(new Coordinates(46.768515, 23.585135));
		//
		// // Craiova [longitude=23.80195, latitude=44.31942]
		// citiesCoords.add(new Coordinates(44.31942, 23.80195));
		//
		// // Brasov [longitude=25.588544, latitude=45.642314]
		// citiesCoords.add(new Coordinates(45.642314, 25.588544));
		//
		// // Ploiesti [longitude=26.023307, latitude=44.940682]
		// citiesCoords.add(new Coordinates(44.940682, 26.023307));
		//
		// // Braila [longitude=27.95651, latitude=45.271135]
		// citiesCoords.add(new Coordinates(45.271135, 27.95651));

		double minDistanceToCity = Double.MAX_VALUE;

		for (Coordinates cityCoords : citiesCoords) {

			double distanceToCity = distance(cityCoords.getLatitude(),
					cityCoords.getLongtude(), sourceLat, sourceLon);

			if (distanceToCity < minDistanceToCity)
				minDistanceToCity = distanceToCity;
		}

		return minDistanceToCity;
	}

	private static double distance(double latitude1, double longitude1,
			double latitude2, double longitude2) {
		double EARTH_RADIUS = 6371 * 1000; // Meters

		latitude1 = Math.toRadians(latitude1);
		longitude1 = Math.toRadians(longitude1);
		latitude2 = Math.toRadians(latitude2);
		longitude2 = Math.toRadians(longitude2);
		double cLa1 = Math.cos(latitude1);
		double x_A = EARTH_RADIUS * cLa1 * Math.cos(longitude1);
		double y_A = EARTH_RADIUS * cLa1 * Math.sin(longitude1);
		double z_A = EARTH_RADIUS * Math.sin(latitude1);
		double cLa2 = Math.cos(latitude2);
		double x_B = EARTH_RADIUS * cLa2 * Math.cos(longitude2);
		double y_B = EARTH_RADIUS * cLa2 * Math.sin(longitude2);
		double z_B = EARTH_RADIUS * Math.sin(latitude2);
		return Math.sqrt((x_A - x_B) * (x_A - x_B) + (y_A - y_B) * (y_A - y_B)
				+ (z_A - z_B) * (z_A - z_B));
	}

}
