package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.DistributionState;

public class OperationGISDeleteNode extends OperationGIS {

	private DistributionState distribStateDistance = null;

	private long id = -1;

	// args
	// -> 0 type
	// -> 1 id
	public OperationGISDeleteNode(long id, String[] args) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		id = Long.parseLong(args[1]);
	}

	public OperationGISDeleteNode(long id, String[] args,
			DistributionState distribStateDistance) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		id = Long.parseLong(args[1]);

		this.distribStateDistance = distribStateDistance;
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();

		try {
			Node deleteNode = db.getNodeById(id);
			for (Relationship rel : deleteNode.getRelationships()) {
				rel.delete();
			}
			deleteNode.delete();

			if (distribStateDistance != null) {
				double deleteNodeVal = distribStateDistance.values.remove(id);
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
