package simulator.gis;

import graph_gen_utils.general.Consts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;
import simulator.gis.astar.Coordinates;
import simulator.gis.astar.GeoCostEvaluator;

public class OperationFactoryGIS implements OperationFactory {

	private DistributionStateGIS distanceDistributionState = new DistributionStateGIS();

	private GraphDatabaseService graphDb = null;

	private long opId = 0;

	private final int ADD_RATIO_INDX = 0;
	private final int DELETE_RATIO_INDX = 1;
	private final int LOCAL_SEARCH_RATIO_INDX = 2;
	private final int GLOBAL_SEARCH_RATIO_INDX = 3;
	private HashMap<Integer, Double> opRatios = new HashMap<Integer, Double>();
	private double sumRatios = 0.0;

	private long opCount = 0;

	public OperationFactoryGIS(GraphDatabaseService graphDb, double addRatio,
			double deleteRatio, double localSearchRatio,
			double globalSearchRatio, long opCount) {

		this.graphDb = graphDb;

		Rnd.initiate(1000);

		populateNodesDistanceFromCity();

		this.opRatios.put(ADD_RATIO_INDX, addRatio);
		this.opRatios.put(DELETE_RATIO_INDX, deleteRatio);
		this.opRatios.put(LOCAL_SEARCH_RATIO_INDX, localSearchRatio);
		this.opRatios.put(GLOBAL_SEARCH_RATIO_INDX, globalSearchRatio);

		this.sumRatios = addRatio + deleteRatio + localSearchRatio
				+ globalSearchRatio;

		this.opCount = opCount;
	}

	public Iterable<Operation> getOperation() {
		return null;
	}

	@Override
	public boolean hasNext() {
		return opId < opCount;
	}

	@Override
	public Operation next() {

		opId++;

		Operation op = null;

		Transaction tx = graphDb.beginTx();
		try {

			op = createOperation();

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			op = null;
		} finally {
			tx.finish();
		}

		return op;
	}

	@Override
	public void shutdown() {
	}

	private Operation createOperation() throws Exception {
		double opTypeRandVal = sumRatios * Rnd.nextDouble(RndType.unif);
		int opTypeIndx = -1;

		for (Entry<Integer, Double> opRatioEntry : opRatios.entrySet()) {
			opTypeRandVal -= opRatioEntry.getValue();
			if (opTypeRandVal <= 0) {
				opTypeIndx = opRatioEntry.getKey();
				break;
			}
		}

		if (opTypeIndx == -1)
			throw new Exception("createOperation() failed");

		long startNodeGid = lookupNodeIdFromDistanceScore(Rnd
				.nextDouble(RndType.unif)
				* distanceDistributionState.sumValues);

		Node startNode = graphDb.getNodeById(startNodeGid);

		switch (opTypeIndx) {
		case ADD_RATIO_INDX:
			Node endNode = getRandNeighbour(startNode);

			double lonStart = (Double) startNode.getProperty(Consts.LONGITUDE);
			double latStart = (Double) startNode.getProperty(Consts.LATITUDE);

			double lonEnd = (Double) endNode.getProperty(Consts.LONGITUDE);
			double latEnd = (Double) endNode.getProperty(Consts.LATITUDE);

			double lonNew = Math.max(lonStart, lonEnd)
					- Math.abs(lonStart - lonEnd);
			double latNew = Math.max(latStart, latEnd)
					- Math.abs(latStart - latEnd);

			// args
			// -> 0 type
			// -> 1 lon
			// -> 2 lan
			// -> 3 startGid
			// -> 4 endGid
			String[] args = new String[] { OperationGISAddNode.class.getName(),
					Double.toString(lonNew), Double.toString(latNew),
					Long.toString(startNode.getId()),
					Long.toString(endNode.getId()) };

			return new OperationGISAddNode(opId, args,
					distanceDistributionState);

		case DELETE_RATIO_INDX:
			return null;

		case LOCAL_SEARCH_RATIO_INDX:
			return null;

		case GLOBAL_SEARCH_RATIO_INDX:
			return null;

		}

		return null;
	}

	private Node getRandNeighbour(Node startNode) {
		ArrayList<Node> neighbours = new ArrayList<Node>();

		for (Relationship rel : startNode.getRelationships())
			neighbours.add(rel.getOtherNode(startNode));

		return neighbours.get((int) Rnd.nextLong(0, neighbours.size() - 1,
				RndType.unif));
	}

	private Long lookupNodeIdFromDistanceScore(double randValue)
			throws Exception {
		for (Entry<Long, Double> distanceEntry : distanceDistributionState.values
				.entrySet()) {
			randValue -= distanceEntry.getValue();
			if (randValue <= 0)
				return distanceEntry.getKey();
		}

		throw new Exception("Node-from-distance lookup failed");
	}

	private void populateNodesDistanceFromCity() {

		distanceDistributionState.values.clear();
		distanceDistributionState.sumValues = 0.0;

		double nodeDistanceToCityScore = 0.0;
		double nodeLon = 0.0;
		double nodeLat = 0.0;

		Transaction tx = graphDb.beginTx();

		try {
			for (Node v : graphDb.getAllNodes()) {
				nodeLon = (Double) v.getProperty(Consts.LONGITUDE);
				nodeLat = (Double) v.getProperty(Consts.LATITUDE);
				nodeDistanceToCityScore = 1 / minDistanceToCityScore(nodeLon,
						nodeLat);

				distanceDistributionState.values.put((Long) v
						.getProperty(Consts.NODE_GID), nodeDistanceToCityScore);
				distanceDistributionState.sumValues += nodeDistanceToCityScore;

			}

			tx.success();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			tx.finish();
		}

		System.out.printf("Entries[%d] SumScores[%f]\n",
				distanceDistributionState.values.size(),
				distanceDistributionState.sumValues);

	}

	// Top Romanian cities, largest-to-smallest
	private double minDistanceToCityScore(double sourceLon, double sourceLat) {

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
