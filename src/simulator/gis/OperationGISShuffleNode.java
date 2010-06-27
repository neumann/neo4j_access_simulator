package simulator.gis;

import java.util.ArrayList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import p_graph_service.PGraphDatabaseService;

import simulator.DistributionState;

public class OperationGISShuffleNode extends OperationGIS {

	private long shuffleId = -1;

	// args
	// -> 0 id
	// -> 1 type
	// -> 2 startId
	public OperationGISShuffleNode(String[] args) {
		super(args);

		this.shuffleId = Long.parseLong(args[2]);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();

		try {
			Node shuffleNode = db.getNodeById(shuffleId);

			long ptn = ((PGraphDatabaseService) db).getPlacementPolicy()
					.getPosition();

			ArrayList<Node> nodesToMove = new ArrayList<Node>();
			nodesToMove.add(shuffleNode);

			((PGraphDatabaseService) db).moveNodes(nodesToMove, ptn);

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
