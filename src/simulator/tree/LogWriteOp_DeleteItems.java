package simulator.tree;

import java.util.Iterator;
import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class LogWriteOp_DeleteItems extends Operation {

	public LogWriteOp_DeleteItems(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		Transaction tx = db.beginTx();
		try {
			Node snode = db.getNodeById(Long.parseLong(args[2]));
			
			if(snode.hasProperty(TreeArgs.name)){
				String name  = (String) snode.getProperty(TreeArgs.name);
				
				// if is a file or a folder fix parent
				// if file fix listLength of parent
				if(name.contains("File")){
					Node parent = snode.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM,Direction.INCOMING).iterator().next().getStartNode();
					int count = (Integer) parent.getProperty(TreeArgs.listLenght);
					parent.setProperty(TreeArgs.listLenght, count-1);
					
				}
				// if folder fix has subfolder of parent
				else if(name.contains("Folder")){
					Node parent = snode.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM,Direction.INCOMING).iterator().next().getStartNode();
					Iterator<Relationship> outRel = parent.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER, Direction.OUTGOING).iterator();
					outRel.next();
					if(!outRel.hasNext()){
						parent.removeProperty(TreeArgs.hasSub);
					}
				}
				
				// delete subtree
				LinkedList<Node> nodesToGo = new LinkedList<Node>();
				nodesToGo.add(snode);
				while (!nodesToGo.isEmpty()) {
					Node n = nodesToGo.remove();
					// delete all incoming relationships
					for(Relationship rs :  n.getRelationships(Direction.INCOMING)){
						rs.delete();
					}
					
					for(Relationship rs : n.getRelationships(Direction.OUTGOING)){
						if(TreeArgs.isEvent(rs)){
							rs.delete();
						}else{
							nodesToGo.add(rs.getEndNode());
						}
					}
				}
			}
			
			
			res = true;
			tx.success();
		} finally{
			tx.finish();
		}
		return res;
	}

}
