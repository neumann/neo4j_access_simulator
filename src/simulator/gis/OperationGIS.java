package simulator.gis;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;

import simulator.Operation;
import simulator.gis.astar.Coordinates;
import simulator.gis.astar.GeoCostEvaluator;

public abstract class OperationGIS extends Operation {

	public OperationGIS(String[] args) {
		super(args);
	}

	@Override
	public abstract boolean onExecute(GraphDatabaseService db);

	// Top Romanian cities, largest-to-smallest
	public static double getMinDistanceToCityScore(double sourceLon,
			double sourceLat) {

		// ArrayList<ArrayList<Double>> citiesCoords = new
		// ArrayList<ArrayList<Double>>();
		ArrayList<Coordinates> citiesCoords = new ArrayList<Coordinates>();

		// Romania [longitude=24.9804, latitude=45.946949]

		// Bucharest [longitude=26.102965, latitude=44.434295]
		citiesCoords.add(new Coordinates(45.946949, 24.9804));

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

			double distanceToCity = GeoCostEvaluator.distance(cityCoords
					.getLatitude(), cityCoords.getLongtude(), sourceLat,
					sourceLon);

			if (distanceToCity < minDistanceToCity)
				minDistanceToCity = distanceToCity;
		}

		return minDistanceToCity;
	}

}
