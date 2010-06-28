package simulator.gis.astar;

import graph_gen_utils.general.Consts;

import org.neo4j.graphalgo.CommonEvaluators;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphalgo.CostEvaluator;
import org.neo4j.graphalgo.EstimateEvaluator;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.kernel.TraversalFactory;

public class AStarRouting {
	private static final EstimateEvaluator<Double> estimateEval = CommonEvaluators
			.geoEstimateEvaluator(Consts.LATITUDE, Consts.LONGITUDE);
	private static final CostEvaluator<Double> costEval = CommonEvaluators
			.doubleCostEvaluator(Consts.WEIGHT);

	public Iterable<Node> doShortestPath(final GraphDatabaseService graphDb,
			Node startNode, Node endNode) throws Exception {

		Expander relExpander = null;
		PathFinder<WeightedPath> sp = null;
		Path path = null;

		try {
			relExpander = TraversalFactory.expanderForTypes(
					GISRelationshipTypes.BICYCLE_WAY, Direction.BOTH);
		} catch (Exception e) {
			throw new Exception(String.format(
					"AStarRouting, error in RelExpander!\n%s", e.getMessage()));

		}

		try {
			sp = GraphAlgoFactory.aStar(relExpander, costEval, estimateEval);
		} catch (Exception e) {
			throw new Exception(String.format(
					"AStarRouting, error in GraphAlgoFactory.aStar!\n%s", e
							.getMessage()));

		}

		try {
			path = sp.findSinglePath(startNode, endNode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(String.format(
					"AStarRouting, error in sp.findSinglePath!\n%s", e
							.getMessage()));

		}

		if (path == null)
			return null;

		return path.nodes();
	}

}