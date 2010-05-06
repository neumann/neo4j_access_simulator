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
				
				// fix parent values
				// this code assumes that there is only one Parent, if there are more it might break
				for(Relationship rs : snode.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.INCOMING)){
					Node parent = rs.getStartNode();
					if(name.contains("File")){
						int count = (Integer) parent.getProperty(TreeArgs.listLenght);
						parent.setProperty(TreeArgs.listLenght, count-1);
					}else if(name.contains("Folder")){
						Iterator<Relationship> outRel = parent.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER, Direction.OUTGOING).iterator();
						outRel.next();
						if(!outRel.hasNext()){
							parent.removeProperty(TreeArgs.hasSub);
						}
					}	
				}
				
				// delete subtree
				LinkedList<Node> nodesToGo = new LinkedList<Node>();
				nodesToGo.add(snode);
				while (!nodesToGo.isEmpty()) {
					Node n = nodesToGo.poll();
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
					n.delete();
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
