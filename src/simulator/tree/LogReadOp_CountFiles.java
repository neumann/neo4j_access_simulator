package simulator.tree;

import java.util.LinkedList;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.Operation;

public class LogReadOp_CountFiles extends Operation {

	public LogReadOp_CountFiles(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean res = false;
		Transaction tx = db.beginTx();
		try {
			
			long fileCount = 0;
			long folderCount = 1;
			LinkedList<Node> nodesToGo = new LinkedList<Node>();
				Node snode = db.getNodeById(Long.parseLong(args[2]));
				snode.getId();
				nodesToGo.add(snode);	
			
			while (!nodesToGo.isEmpty()) {
				Node n = nodesToGo.remove();
				if(n.hasProperty(TreeArgs.hasSub)){
					fileCount += (Integer)n.getProperty(TreeArgs.listLenght);
					
					for(Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER)){
						nodesToGo.add(rs.getEndNode());
						folderCount ++;	
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
