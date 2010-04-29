package simulator.gis.astar;

import org.neo4j.graphdb.RelationshipType;

public enum GISRelationshipTypes implements RelationshipType {
	FOOT_WAY, BICYCLE_WAY, CAR_WAY, CAR_SHORTEST_WAY
}
