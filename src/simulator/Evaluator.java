package simulator;

import org.neo4j.graphdb.Node;

public abstract class Evaluator {
	public abstract double evaluate(Node n);
}
