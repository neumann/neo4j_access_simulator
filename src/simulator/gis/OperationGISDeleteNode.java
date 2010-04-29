package simulator.gis;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

public class OperationGISDeleteNode extends OperationGIS {

	private long gid = -1;

	// args
	// -> 0 type
	// -> 4 gid
	public OperationGISDeleteNode(long id, String[] args) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		gid = Long.parseLong(args[1]);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();

		try {
			Node deleteNode = db.getNodeById(gid);
			for (Relationship rel : deleteNode.getRelationships()) {
				rel.delete();
			}
			deleteNode.delete();

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
