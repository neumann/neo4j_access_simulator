package simulator.gis;

import graph_gen_utils.general.Consts;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.gis.astar.AStarRouting;
import org.neo4j.graphalgo.impl.util.GeoEstimateEvaluator;

//import simulator.gis.astar.GeoCostEvaluator;

public abstract class OperationGISShortestPath extends OperationGIS {

	private long startId = -1;
	private long endId = -1;

	// args
	// -> 0 id
	// -> 1 type
	// -> 2 startId
	// -> 3 endId
	public OperationGISShortestPath(String[] args) {
		super(args);

		startId = Long.parseLong(args[2]);
		endId = Long.parseLong(args[3]);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();
		try {
			Node startNode = db.getNodeById(startId);
			Node endNode = db.getNodeById(endId);

			AStarRouting astar = new AStarRouting();
			Iterable<Node> pathNodes = astar.doShortestPath(db, startNode,
					endNode);

			String pathStr = "";
			Integer pathLen = 0;
			if (pathNodes != null)
				for (Node node : pathNodes) {
					pathLen++;
					pathStr = pathStr + "," + node.getId();
				}

			// NOTE Commented to clean up logs
			// this.info.put(GIS_PATH_LENGTH_TAG, pathLen.toString());
			// this.info.put(GIS_PATH_TAG, pathStr);

			// NOTE OLD
			// Double distance = GeoCostEvaluator.distance((Double) startNode
			// .getProperty(Consts.LATITUDE), (Double) startNode
			// .getProperty(Consts.LONGITUDE), (Double) endNode
			// .getProperty(Consts.LATITUDE), (Double) endNode
			// .getProperty(Consts.LONGITUDE));
			// NODE NEW
			GeoEstimateEvaluator geoEval = new GeoEstimateEvaluator(
					Consts.LATITUDE, Consts.LONGITUDE);
			Double distance = geoEval.getCost(startNode, endNode);

			// NOTE Commented to clean up logs
			// this.info.put(GIS_DISTANCE_TAG, distance.toString());

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			tx.finish();
		}

		return result;
	}

}
