package simulator.tree;

public class Distribution{
	final double countOp;
	final double searchOp;
	final double addOp;
	final double delOp;
	final double shuffleOp;
	
	public Distribution(double count, double search, double add, double del, double shuffle) {
		delOp = del;
		addOp = delOp + add; 
		searchOp = addOp + search;
		countOp = searchOp + count;
		shuffleOp = countOp+shuffle;
	}
	
}
