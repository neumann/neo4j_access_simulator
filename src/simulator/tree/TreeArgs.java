package simulator.tree;

import org.neo4j.graphdb.RelationshipType;

public class TreeArgs {
	
	public enum TreeRelTypes implements RelationshipType
	{
		REF_ORGANISATIONS, ORGANISATION, ORGANISATION_TO_USER, USER_FOLDER_ROOT, CHILD_ITEM, CHILD_FOLDER
	}
	
	public static String LogSeperator = "*;";
}


