package simulator.tree.operations;

import java.util.LinkedList;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

import simulator.Operation;
import simulator.tree.TreeArgs;

public class CountFiles_ReadOp extends Operation {

	public CountFiles_ReadOp(String[] args) {
		super(args);
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {		
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
					
					for(Relationship rs : n.getRelationships(TreeArgs.TreeRelTypes.CHILD_FOLDER, Direction.OUTGOING)){
						nodesToGo.add(rs.getEndNode());
						folderCount ++;	
					}	
				}
			}
			
			return true;
		}catch (Exception e) {
			//do nothing
		}
		return false;
	}

}
