package simulator.tree;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class LogReadOp_SearchFiles extends Operation {

	public LogReadOp_SearchFiles(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		Transaction tx = db.beginTx();
		try {
			LinkedList<Node> nodesToGo = new LinkedList<Node>();
			
			Node snode = db.getNodeById(Long.parseLong(args[2]));
			nodesToGo.add(snode);	
			long enodeID = Long.parseLong(args[3]);
				
			while (!nodesToGo.isEmpty() && !res) {
				Node n = nodesToGo.remove();
				for(Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_ITEM, Direction.OUTGOING)){
					if(n.getId() == enodeID){
						res = true;
//						nodesToGo.clear();
						break;
					}else{
						nodesToGo.add(rs.getEndNode());		
					}
				}
			}
			tx.success();
		} finally{
			tx.finish();
		}
		return res;
	}

}
