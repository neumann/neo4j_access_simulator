package simulator.gis;

public class OperationFactoryGISConfig {
	private double addRatio = 0d;
	private double delRatio = 0d;
	private double shortRatio = 0d;
	private double longRatio = 0d;
	private double shuffleRatio = 0d;
	private long opCount = 0;
	private String operationLogOut = null;

	public OperationFactoryGISConfig(double addRatio, double delRatio,
			double shortRatio, double longRatio, double shuffleRatio,
			long opCount, String operationLogOut) {
		super();
		this.addRatio = addRatio;
		this.delRatio = delRatio;
		this.shortRatio = shortRatio;
		this.longRatio = longRatio;
		this.shuffleRatio = shuffleRatio;
		this.opCount = opCount;
		this.operationLogOut = operationLogOut;
	}

	public double getAddRatio() {
		return addRatio;
	}

	public double getDelRatio() {
		return delRatio;
	}

	public double getShortRatio() {
		return shortRatio;
	}

	public double getLongRatio() {
		return longRatio;
	}

	public double getShuffleRatio() {
		return shuffleRatio;
	}

	public long getOpCount() {
		return opCount;
	}

	public String getOperationLogOut() {
		return operationLogOut;
	}

}
