package simulator.gis;

import graph_gen_utils.general.Consts;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.gis.astar.AStarRouting;
import org.neo4j.graphalgo.impl.util.GeoEstimateEvaluator;

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

			Iterable<Node> pathNodes = null;

			try {
				AStarRouting astar = new AStarRouting();
				pathNodes = astar.doShortestPath(db, startNode, endNode);
			} catch (Exception e) {
				throw new Exception(
						String
								.format(
										"ShortestPathLong, error in AStar!\n\tstartNode[%s] endNode[%s]\n%s",
										startNode, endNode, e.getMessage()));
			}

			String pathStr = "";
			Integer pathLen = 0;
			try {
				if (pathNodes != null)
					for (Node node : pathNodes) {
						pathLen++;
						pathStr = pathStr + "," + node.getId();
					}
			} catch (Exception e) {
				throw new Exception(
						String
								.format(
										"ShortestPathLong, error in PathLen calculation\n\tstartNode[%s] endNode[%s]\n%s",
										startNode, endNode, e.getMessage()));
			}

			// NOTE Commented to clean up logs
			// this.info.put(GIS_PATH_LENGTH_TAG, pathLen.toString());
			// this.info.put(GIS_PATH_TAG, pathStr);

			GeoEstimateEvaluator geoEval = new GeoEstimateEvaluator(
					Consts.LATITUDE, Consts.LONGITUDE);
			Double distance = -1d;
			try {
				distance = geoEval.getCost(startNode, endNode);
			} catch (Exception e) {
				throw new Exception(
						String
								.format(
										"ShortestPathLong, error in Distance calculation\n\tstartNode[%s] endNode[%s]\n%s",
										startNode, endNode, e.getMessage()));
			}

			// NOTE Commented to clean up logs
			// this.info.put(GIS_DISTANCE_TAG, distance.toString());

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			tx.failure();
			result = false;
		} finally {
			tx.finish();
		}

		return result;
	}

}
