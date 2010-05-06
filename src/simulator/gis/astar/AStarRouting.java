package simulator.gis.astar;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import graph_gen_utils.general.Consts;

import org.neo4j.graphalgo.shortestpath.AStar;
import org.neo4j.graphalgo.shortestpath.CostEvaluator;
import org.neo4j.graphalgo.shortestpath.EstimateEvaluator;
import org.neo4j.graphalgo.shortestpath.std.DoubleEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.DefaultExpander;

public class AStarRouting {
	private static final EstimateEvaluator<Double> estimateEval = new GeoCostEvaluator();
	private static final CostEvaluator<Double> costEval = new DoubleEvaluator(
			Consts.WEIGHT);

	private LinkedHashMap<Long, Integer> nodesDegree = new LinkedHashMap<Long, Integer>();
	private Long sumDegrees = (long) 0;

	private LinkedHashMap<Long, Double> nodesClusterCoefficient = new LinkedHashMap<Long, Double>();
	private double sumClusterCoefficients = 0.0;

	public Iterable<Node> doShortestPath(final GraphDatabaseService graphDb,
			Node startNode, Node endNode) {

		DefaultExpander relExpander = new DefaultExpander();
		// relExpander.add( GISRelationshipTypes.FOOT_WAY, Direction.BOTH );
		relExpander.add(GISRelationshipTypes.BICYCLE_WAY, Direction.BOTH);
		// relExpander.add( GISRelationshipTypes.CAR_WAY, Direction.BOTH );
		// relExpander.add( GISRelationshipTypes.CAR_SHORTEST_WAY,
		// Direction.BOTH );

		AStar sp = new AStar(graphDb, relExpander, costEval, estimateEval);

		Path path = sp.findSinglePath(startNode, endNode);

		return path.nodes();
	}

	private void populateNodesClusterCoefficient(GraphDatabaseService transNeo) {

		nodesClusterCoefficient.clear();
		sumClusterCoefficients = 0.0;

		Transaction tx = transNeo.beginTx();

		try {
			for (Node v : transNeo.getAllNodes()) {

				long nodeDegree = 0;
				long nodeNeighbourRels = 0;
				ArrayList<Node> nodeNeighbours = new ArrayList<Node>();
				ArrayList<Long> nodeNeighboursIDs = new ArrayList<Long>();

				for (Relationship e : v.getRelationships(Direction.BOTH)) {

					Node u = e.getOtherNode(v);

					nodeDegree++;

					nodeNeighbours.add(u);
					nodeNeighboursIDs.add(u.getId());

				}

				for (Node nodeNeighbour : nodeNeighbours) {
					for (Relationship e : nodeNeighbour
							.getRelationships(Direction.BOTH)) {

						Node nodeNeighboursNeighbour = e
								.getOtherNode(nodeNeighbour);

						// my neighbour neighbours my other neighbours
						if (nodeNeighboursIDs.contains(nodeNeighboursNeighbour
								.getId()))
							nodeNeighbourRels++;

					}
				}

				double nodeClusterCoefficient = 0.0;
				if (nodeDegree > 0)
					nodeClusterCoefficient = nodeNeighbourRels
							/ ((double) nodeDegree * ((double) nodeDegree - 1));

				sumClusterCoefficients += nodeClusterCoefficient;

				nodesClusterCoefficient.put(v.getId(), nodeClusterCoefficient);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			tx.finish();
		}

	}

	private void populateNodesDegree(GraphDatabaseService transNeo) {

		nodesDegree.clear();
		sumDegrees = (long) 0;

		Transaction tx = transNeo.beginTx();

		try {
			for (Node v : transNeo.getAllNodes()) {

				int nodeDegree = 0;

				for (Relationship e : v.getRelationships(Direction.BOTH)) {

					nodeDegree++;

				}

				sumDegrees += nodeDegree;

				nodesDegree.put(v.getId(), nodeDegree);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			tx.finish();
		}

	}

}