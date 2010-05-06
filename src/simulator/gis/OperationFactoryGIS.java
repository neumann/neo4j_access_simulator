package simulator.gis;

import graph_gen_utils.general.Consts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.neo4j.graphalgo.centrality.NetworkDiameter;
import org.neo4j.graphalgo.shortestpath.CostAccumulator;
import org.neo4j.graphalgo.shortestpath.CostEvaluator;
import org.neo4j.graphalgo.shortestpath.Dijkstra;
import org.neo4j.graphalgo.shortestpath.EstimateEvaluator;
import org.neo4j.graphalgo.shortestpath.PathFinder;
import org.neo4j.graphalgo.shortestpath.SingleSourceShortestPathDijkstra;
import org.neo4j.graphalgo.shortestpath.std.DoubleEvaluator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;

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

			Node endNode = doRandomWalk(startNode, 1);

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

				// FIXME change walkLength to a random value, maybe between 1 &
				// Network Diameter?
				int walkLength = 10;
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

	private long getNetworkDiameter(GraphDatabaseService graphDb) {
		//
		// CostEvaluator<Integer> costEval = new CostEvaluator<Integer>() {
		//
		// @Override
		// public Integer getCost(Relationship relationship, boolean backwards)
		// {
		// return 1;
		// }
		// };
		//
		// CostAccumulator<Integer> costAcc = new CostAccumulator<Integer>() {
		//
		// @Override
		// public Integer addCosts(Integer c1, Integer c2) {
		// return c1 + c2;
		// }
		// };
		//
		// Comparator<Integer> costComp = new Comparator<Integer>() {
		//
		// @Override
		// public int compare(Integer arg0, Integer arg1) {
		// if (arg0 > arg1)
		// return -1;
		// if (arg0 < arg1)
		// return 1;
		// return 0;
		// }
		// };
		//
		// Node startNode = graphDb.getReferenceNode();
		// Integer startCost = 0;
		// SingleSourceShortestPathDijkstra<Integer> singleSourceShortestPath =
		// new SingleSourceShortestPathDijkstra<Integer>(
		// startCost, startNode, costEval, costAcc, costComp,
		// Direction.BOTH, RelationshipExpander.forTypes(
		// GISRelationshipTypes.FOOT_WAY, Direction.BOTH,
		// GISRelationshipTypes.BICYCLE_WAY, Direction.BOTH,
		// GISRelationshipTypes.CAR_WAY, Direction.BOTH,
		// GISRelationshipTypes.CAR_SHORTEST_WAY, Direction.BOTH));
		//
		// Integer zeroValue = 0;
		//
		// Set<Node> nodeSet = graphDb.getAllNodes();
		//
		// NetworkDiameter<Integer> networkDiameter = new
		// NetworkDiameter<Integer>(
		// singleSourceShortestPath, zeroValue, nodeSet, costComp);

		return 0;
	}

	private static String getTimeStr(long msTotal) {
		long ms = msTotal % 1000;
		long s = (msTotal / 1000) % 60;
		long m = (msTotal / 1000) / 60;

		return String.format("%d(m):%d(s):%d(ms)%n", m, s, ms);
	}

}
