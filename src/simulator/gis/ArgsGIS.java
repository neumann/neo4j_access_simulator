package simulator.gis;

import org.neo4j.graphdb.RelationshipType;

public class ArgsGIS {

	public enum GISRelTypes implements RelationshipType {
		FOOT_WAY, BICYCLE_WAY, CAR_WAY, CAR_SHORTEST_WAY
	}

	public static String LogSeperator = ";";
}
