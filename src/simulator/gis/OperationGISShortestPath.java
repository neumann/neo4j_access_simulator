package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.gis.astar.AStarRouting;

public class OperationGISShortestPath extends OperationGIS {

	private long startGid = -1;
	private long endGid = -1;

	public OperationGISShortestPath(long id, String[] args) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		startGid = Long.parseLong(args[1]);
		endGid = Long.parseLong(args[2]);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();
		try {
			Node startNode = db.getNodeById(startGid);
			Node endNode = db.getNodeById(endGid);

			AStarRouting astar = new AStarRouting();
			astar.doShortestPath(db, startNode, endNode);

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
