package simulator.tree.operations;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import simulator.Operation;
import simulator.tree.TreeArgs;

public class SearchFiles_ReadOp extends Operation {

	public SearchFiles_ReadOp(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {

		try {
			LinkedList<Node> nodesToGo = new LinkedList<Node>();

			Node snode = db.getNodeById(Long.parseLong(args[2]));
			long enodeID = Long.parseLong(args[3]);
			if (snode.getId() == enodeID) {
				return true;
			}
			nodesToGo.add(snode);

			while (!nodesToGo.isEmpty()) {
				Node n = nodesToGo.remove();
//				TreeSet<Relationship> rsSet = new TreeSet<Relationship>();
//				for (Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.OUTGOING)) {
//					rsSet.add(rs);
//				}
				
				for (Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.OUTGOING)){
					Node endN = rs.getEndNode();
					if (endN.getId() == enodeID) {
						return true;
					} else {
						nodesToGo.add(endN);
					}
				}
			}
		} catch (Exception e) {
			// do nothing something went wrong so false should be returned
		}

		return false;
	}

}
