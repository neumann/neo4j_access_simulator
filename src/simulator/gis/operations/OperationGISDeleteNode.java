package simulator.gis.operations;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.DistributionState;

public class OperationGISDeleteNode extends OperationGIS {

	private DistributionState distribStateDistance = null;

	private long startId = -1;

	// args
	// -> 0 id
	// -> 1 type
	// -> 2 startId
	public OperationGISDeleteNode(String[] args) {
		super(args);

		this.startId = Long.parseLong(args[2]);
	}

	public OperationGISDeleteNode(String[] args,
			DistributionState distribStateDistance) {
		super(args);

		this.startId = Long.parseLong(args[2]);

		this.distribStateDistance = distribStateDistance;
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();

		try {
			Node deleteNode = db.getNodeById(startId);
			for (Relationship rel : deleteNode.getRelationships()) {
				rel.delete();
			}
			deleteNode.delete();

			if (distribStateDistance != null) {
				double deleteNodeVal = distribStateDistance.values
						.remove(startId);
				distribStateDistance.sumValues -= deleteNodeVal;
			}

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
