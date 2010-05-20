package simulator.tree;

import java.util.Iterator;
import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;
import simulator.tree.TreeArgs.TreeRelTypes;

public class DeleteItems_WriteOp extends Operation {

	public DeleteItems_WriteOp(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		
		Transaction tx = db.beginTx();
		try {
			Node snode = db.getNodeById(Long.parseLong(args[2]));

			if (!snode.hasProperty(TreeArgs.name)){
				return false;
			}
			
				String name = (String) snode.getProperty(TreeArgs.name);

				// fix parent values
				// this code assumes that there is only one Parent, if there are
				// more it might break
				for (Relationship rs : snode.getRelationships(
						TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.INCOMING)) {
					Node parent = rs.getStartNode();
					if (name.contains("File")) {
						int count = (Integer) parent
								.getProperty(TreeArgs.listLenght);
						parent.setProperty(TreeArgs.listLenght, count - 1);
					} else if (name.contains("Folder")) {
						Iterator<Relationship> outRel = parent
								.getRelationships(
										TreeArgs.TreeRelTypes.CHILD_FOLDER,
										Direction.OUTGOING).iterator();
						outRel.next();
						if (!outRel.hasNext()) {
							parent.removeProperty(TreeArgs.hasSub);
						}
					} else {
						throw new Error("can only delete Files or Folders");
					}
				}
				
//				// commit
//				tx.success();
//				tx.finish();
//				tx = db.beginTx();

				// delete subtree
				LinkedList<Node> nodesToGo = new LinkedList<Node>();
				nodesToGo.add(snode);
				while (!nodesToGo.isEmpty()) {
					Node n = nodesToGo.remove();
					for (Relationship rs : n
							.getRelationships(TreeRelTypes.CHILD_ITEM,Direction.OUTGOING)) {
						nodesToGo.add(rs.getEndNode());
					}
					
					for(Relationship rs : n.getRelationships()){
						rs.delete();
					}
					n.delete();
				}
			res = true;
			tx.success();
		} finally {
			tx.finish();
		}
		return res;
	}

}
