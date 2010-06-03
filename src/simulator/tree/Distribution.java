package simulator.tree;

public class Distribution{
	final double countOp;
	final double searchOp;
	final double addOp;
	final double delOp;
	
	public Distribution(double count, double search, double add, double del) {
		delOp = del;
		addOp = delOp + add;
		searchOp = addOp + search;
		countOp = searchOp + count;
	}
	
}
