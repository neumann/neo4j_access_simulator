package simulator.gis;

import graph_gen_utils.general.Consts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.neo4j.graphalgo.centrality.NetworkDiameter;
import org.neo4j.graphalgo.shortestpath.CostAccumulator;
import org.neo4j.graphalgo.shortestpath.CostEvaluator;
import org.neo4j.graphalgo.shortestpath.Dijkstra;
import org.neo4j.graphalgo.shortestpath.EstimateEvaluator;
import org.neo4j.graphalgo.shortestpath.PathFinder;
import org.neo4j.graphalgo.shortestpath.SingleSourceShortestPath;
import org.neo4j.graphalgo.shortestpath.SingleSourceShortestPathDijkstra;
import org.neo4j.graphalgo.shortestpath.std.DoubleAdder;
import org.neo4j.graphalgo.shortestpath.std.DoubleComparator;
import org.neo4j.graphalgo.shortestpath.std.DoubleEvaluator;
import org.neo4j.graphalgo.shortestpath.SingleStepShortestPathsFinder;
import org.neo4j.graphalgo.shortestpath.std.IntegerAdder;
import org.neo4j.graphalgo.shortestpath.std.IntegerComparator;
import org.neo4j.graphalgo.shortestpath.std.IntegerEvaluator;
import org.neo4j.graphalgo.centrality.Eccentricity;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.DefaultExpander;

import simulator.DistributionState;
import simulator.Operation;
import simulator.OperationFactory;
import simulator.Rnd;
import simulator.Rnd.RndType;
import simulator.gis.astar.Coordinates;
import simulator.gis.astar.GISRelationshipTypes;
import simulator.gis.astar.GeoCostEvaluator;

public class OperationFactoryGIS implements OperationFactory {

	private DistributionState distanceDistributionState = new DistributionState();

	private GraphDatabaseService graphDb = null;

	private Long opId = new Long(0);

	private final int ADD_RATIO_INDX = 0;
	private final int DELETE_RATIO_INDX = 1;
	private final int LOCAL_SEARCH_RATIO_INDX = 2;
	private final int GLOBAL_SEARCH_RATIO_INDX = 3;
	private HashMap<Integer, Double> opRatios = new HashMap<Integer, Double>();
	private double sumRatios = 0.0;

	private long opCount = 0;

	private final static double earthRadius = 6378.7; // km
	private final static double earthCircumpherence = Math.PI * earthRadius * 2;
	private final static double kmsInDeg = 360 / earthCircumpherence;

	// City population ~= 25,000 (Bucharest ~= 26,911)
	// Cluster Coefficient ~= 0 for entire graph
	// Assume minimal cycles
	// |V| ~= 700,000
	// |E| ~= 7,000,000
	// Edges duplicated 4x (foot, cycle, car, shortest car)
	// Average degree = 2.5 -> Assume average degree = 3
	// Assume a city is a balanced tree as much as possible with 25,000 nodes
	// (ignore cycles/shortcuts)
	// Tree depth = 11
	// City Diameter = 2 x Tree depth = 22
	// Assume trip starts in city center and doesn't leave city on average
	// Average path length is therefore 11
	private final static int averagePathLength = 11;

	public OperationFactoryGIS(GraphDatabaseService graphDb, double addRatio,
			double deleteRatio, double localSearchRatio,
			double globalSearchRatio, long opCount) {

		this.graphDb = graphDb;

		long time = System.currentTimeMillis();
		System.out.printf("Populating distance scores...");

		populateNodesDistanceFromCity();

		System.out.printf("%s", getTimeStr(System.currentTimeMillis() - time));

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
		int opTypeIndx = getRandomOperationType();

		switch (opTypeIndx) {

		case ADD_RATIO_INDX: {
			Object[] results = Rnd.getSampleFromMap(
					distanceDistributionState.values,
					distanceDistributionState.sumValues, 1, RndType.unif);

			long startNodeId = (Long) results[0];

			Node startNode = graphDb.getNodeById(startNodeId);

			if (startNode == null)
				throw new Exception(String.format("startNode[%d] == null",
						startNodeId));

			Node endNode = doRandomWalk(startNode, 1);

			if (endNode == null)
				throw new Exception("endNode == null");

			double lonStart = (Double) startNode.getProperty(Consts.LONGITUDE);
			double latStart = (Double) startNode.getProperty(Consts.LATITUDE);

			double lonEnd = (Double) endNode.getProperty(Consts.LONGITUDE);
			double latEnd = (Double) endNode.getProperty(Consts.LATITUDE);

			double lonNew = Math.max(lonStart, lonEnd)
					- Math.abs(lonStart - lonEnd);
			double latNew = Math.max(latStart, latEnd)
					- Math.abs(latStart - latEnd);

			// args
			// -> 0 id
			// -> 1 type
			// -> 2 lon
			// -> 3 lan
			// -> 4 startGid
			// -> 5 endGid
			String[] args = new String[] { opId.toString(),
					OperationGISAddNode.class.getName(),
					Double.toString(lonNew), Double.toString(latNew),
					Long.toString(startNodeId), Long.toString(endNode.getId()) };

			return new OperationGISAddNode(args, distanceDistributionState);
		}

		case DELETE_RATIO_INDX: {
			Object[] results = Rnd.getSampleFromMap(
					distanceDistributionState.values,
					distanceDistributionState.sumValues, 1, RndType.unif);

			long startNodeId = (Long) results[0];

			Node startNode = graphDb.getNodeById(startNodeId);

			if (startNode == null)
				throw new Exception(String.format("startNode[%d] == null",
						startNodeId));

			// args
			// -> 0 id
			// -> 1 type
			// -> 2 startId
			String[] args = new String[] { opId.toString(),
					OperationGISDeleteNode.class.getName(),
					Long.toString(startNode.getId()) };

			return new OperationGISDeleteNode(args, distanceDistributionState);
		}

		case LOCAL_SEARCH_RATIO_INDX: {
			long startNodeId = -1;
			Node startNode = null;
			Node endNode = null;

			while (endNode == null) {
				Object[] results = Rnd.getSampleFromMap(
						distanceDistributionState.values,
						distanceDistributionState.sumValues, 1, RndType.unif);

				startNodeId = (Long) results[0];
				startNode = graphDb.getNodeById(startNodeId);

				if (startNode == null)
					throw new Exception(String.format("startNode[%d] == null",
							startNodeId));

				int walkLength = (int) (Rnd.nextDouble(RndType.expo,
						1d / averagePathLength));
				endNode = doRandomWalk(startNode, walkLength);
			}

			// args
			// -> 0 id
			// -> 1 type
			// -> 2 startId
			// -> 3 endId
			String[] args = new String[] { opId.toString(),
					OperationGISShortestPathLocal.class.getName(),
					Long.toString(startNodeId), Long.toString(endNode.getId()) };

			return new OperationGISShortestPathLocal(args);
		}

		case GLOBAL_SEARCH_RATIO_INDX: {
			Object[] results = Rnd.getSampleFromMap(
					distanceDistributionState.values,
					distanceDistributionState.sumValues, 1, RndType.unif);

			long startNodeId = (Long) results[0];
			long endNodeId = startNodeId;

			while (endNodeId == startNodeId) {
				results = Rnd.getSampleFromMap(
						distanceDistributionState.values,
						distanceDistributionState.sumValues, 1, RndType.unif);
				endNodeId = (Long) results[0];
			}

			Node startNode = graphDb.getNodeById(startNodeId);

			if (startNode == null)
				throw new Exception(String.format("startNode[%d] == null",
						startNodeId));

			Node endNode = graphDb.getNodeById(endNodeId);

			if (endNode == null)
				throw new Exception(String.format("endNode[%d] == null",
						endNodeId));

			// args
			// -> 0 id
			// -> 1 type
			// -> 2 startId
			// -> 3 endId
			String[] args = new String[] { opId.toString(),
					OperationGISShortestPathGlobal.class.getName(),
					Long.toString(startNodeId), Long.toString(endNodeId) };

			return new OperationGISShortestPathGlobal(args);
		}

		}

		return null;
	}

	private int getRandomOperationType() throws Exception {
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

		return opTypeIndx;
	}

	private Node doRandomWalk(Node startNode, int steps) {
		Node randNode = startNode;

		for (int i = 0; i < steps; i++) {

			ArrayList<Node> neighbours = new ArrayList<Node>();

			for (Relationship rel : randNode.getRelationships()) {
				Node otherNode = rel.getOtherNode(randNode);

				if (otherNode.getId() == startNode.getId())
					continue;

				neighbours.add(otherNode);
			}

			if (neighbours.size() == 0)
				continue;

			randNode = neighbours.get((int) Rnd.nextLong(0,
					neighbours.size() - 1, RndType.unif));
		}

		if (randNode.getId() == startNode.getId())
			return null;

		return randNode;
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
				nodeDistanceToCityScore = 1 / OperationGIS
						.getMinDistanceToCityScore(nodeLon, nodeLat);

				distanceDistributionState.values.put(v.getId(),
						nodeDistanceToCityScore);
				distanceDistributionState.sumValues += nodeDistanceToCityScore;

			}

			tx.success();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			tx.finish();
		}

		System.out.printf("Values[%d] SumValues[%f]...",
				distanceDistributionState.values.size(),
				distanceDistributionState.sumValues);

	}

	public static int getBucharestDiameter(GraphDatabaseService graphDb) {
		// Bucharest [longitude=26.102965, latitude=44.434295]
		// Bucharest Area = 285 km²
		return getNetworkDiameterForCity(graphDb, 26.102965, 44.434295, 285);
	}

	// Bucharest [longitude=26.102965, latitude=44.434295]
	// Bucharest Area = 285 km²
	private static int getNetworkDiameterForCity(GraphDatabaseService graphDb,
			double midLon, double midLat, double cityArea) {

		long time = System.currentTimeMillis();
		System.out.printf("Calculating city borders...");

		double cityWidth = Math.sqrt(cityArea); // km
		double minLon = midLon - ((cityWidth / 2) * kmsInDeg);
		double maxLon = midLon + ((cityWidth / 2) * kmsInDeg);

		double cityHeight = Math.sqrt(cityArea); // km
		double minLat = midLat - ((cityHeight / 2) * kmsInDeg);
		double maxLat = midLat + ((cityHeight / 2) * kmsInDeg);

		System.out.printf("%s", getTimeStr(System.currentTimeMillis() - time));
		System.out.printf("\tLon [%f - %f]\n", minLon, maxLon);
		System.out.printf("\tLat [%f - %f]\n", minLat, maxLat);

		time = System.currentTimeMillis();
		System.out.printf("Finding related Nodes...");

		HashSet<Node> nodes = getCityNodes(graphDb, minLon, maxLon, minLat,
				maxLat);

		System.out.printf("[%d]...%s", nodes.size(), getTimeStr(System
				.currentTimeMillis()
				- time));

		return getNetworkDiameter(nodes);
	}

	private static HashSet<Node> getCityNodes(GraphDatabaseService graphDb,
			double minLon, double maxLon, double minLat, double maxLat) {

		HashSet<Node> nodes = new HashSet<Node>();

		Transaction tx = graphDb.beginTx();
		try {

			for (Node node : graphDb.getAllNodes()) {
				double nodeLon = (Double) node.getProperty(Consts.LONGITUDE);
				double nodeLat = (Double) node.getProperty(Consts.LATITUDE);
				if ((minLon < nodeLon) && (nodeLon <= maxLon)
						&& (minLat < nodeLat) && (nodeLat <= maxLat))
					nodes.add(node);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.finish();
		}

		return nodes;
	}

	private static int getNetworkDiameter(HashSet<Node> nodes) {

		CostEvaluator<Integer> costEval = new CostEvaluator<Integer>() {

			@Override
			public Integer getCost(Relationship relationship, boolean backwards) {
				return 1;
			}
		};

		CostAccumulator<Integer> costAcc = new IntegerAdder();

		Comparator<Integer> costComp = new IntegerComparator();

		Node startNode = nodes.iterator().next();
		Integer startCost = 0;
		SingleSourceShortestPath<Integer> singleSourceShortestPath = new SingleSourceShortestPathDijkstra<Integer>(
				startCost, startNode, costEval, costAcc, costComp,
				Direction.BOTH, GISRelationshipTypes.BICYCLE_WAY);

		Integer zeroValue = 0;

		// NetworkDiameter<Integer> networkDiameter = new
		// NetworkDiameter<Integer>(
		// singleSourceShortestPath, zeroValue, nodes, costComp);

		Eccentricity<Integer> eccentricity = new Eccentricity<Integer>(
				singleSourceShortestPath, zeroValue, nodes, costComp);

		long time = System.currentTimeMillis();
		System.out.printf("Calculating Centralities...");

		// networkDiameter.calculate();
		eccentricity.processShortestPaths(nodes.iterator().next(),
				singleSourceShortestPath);

		System.out.printf("...%s",
				getTimeStr(System.currentTimeMillis() - time));

		time = System.currentTimeMillis();
		System.out.printf("Calculating Diameter...");

		int diameter = 0;
		for (Node node : nodes) {
			// int nodeCentrality = networkDiameter.getCentrality(node);
			int nodeCentrality = (Integer) eccentricity.getCentrality(node);
			if (nodeCentrality > diameter)
				diameter = nodeCentrality;
		}

		System.out.printf("[%d]...%s", nodes.size(), getTimeStr(System
				.currentTimeMillis()
				- time));

		return diameter;
	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
