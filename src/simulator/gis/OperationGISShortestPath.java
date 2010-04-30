package simulator.gis;

import graph_gen_utils.general.Consts;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.gis.astar.AStarRouting;
import simulator.gis.astar.GeoCostEvaluator;

public class OperationGISShortestPath extends OperationGIS {

	private long startId = -1;
	private long endId = -1;

	// args
	// -> 0 type
	// -> 1 startId
	// -> 2 endId
	public OperationGISShortestPath(long id, String[] args) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		startId = Long.parseLong(args[1]);
		endId = Long.parseLong(args[2]);
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

			Integer pathLen = 0;
			for (Node node : pathNodes) {
				pathLen++;
			}

			this.info.put(GIS_PATH_LENGTH_TAG, pathLen.toString());

			Double distance = GeoCostEvaluator.distance((Double) startNode
					.getProperty(Consts.LATITUDE), (Double) startNode
					.getProperty(Consts.LONGITUDE), (Double) endNode
					.getProperty(Consts.LATITUDE), (Double) endNode
					.getProperty(Consts.LONGITUDE));

			this.info.put(GIS_DISTANCE_TAG, distance.toString());

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
