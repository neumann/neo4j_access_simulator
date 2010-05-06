package simulator.tree;

import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;

public class TreeArgs {
	public static final String listLenght = "list_length";
	public static final String hasSub = "has_sub_folders";
	public static final String name = "name";
	public static final String size = "size";
	
	public enum TreeRelTypes implements RelationshipType
	{
		CHILD_FOLDER,
		ACTUAL_EVENT,
		ORGANISATION_TO_USER,
		USER_FOLDER_ROOT,
		CHILD_ITEM,
		EVENT,
		AGGREGATED_EVENT,
		REF_ORGANISATIONS,
		ORGANISATION
	}
	
	public static boolean isEvent(Relationship rs){
		if(rs.getType().equals(TreeRelTypes.EVENT))return true;
		if(rs.getType().equals(TreeRelTypes.ACTUAL_EVENT))return true;
		if(rs.getType().equals(TreeRelTypes.AGGREGATED_EVENT))return true;
		return false;
	}
	
	public static String LogSeperator = "*;";
}


