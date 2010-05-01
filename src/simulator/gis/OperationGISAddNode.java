package simulator.gis;

import graph_gen_utils.general.Consts;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import simulator.DistributionState;

public class OperationGISAddNode extends OperationGIS {

	private DistributionState distribStateDistance = null;

	private double lat = 0.0;
	private double lon = 0.0;
	private long startGid = -1;
	private long endGid = -1;

	// args
	// -> 0 type
	// -> 1 lon
	// -> 2 lan
	// -> 3 startGid
	// -> 4 endGid
	public OperationGISAddNode(long id, String[] args) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		this.lon = Double.parseDouble(args[1]);
		this.lat = Double.parseDouble(args[2]);
		this.startGid = Long.parseLong(args[3]);
		this.endGid = Long.parseLong(args[4]);
	}

	public OperationGISAddNode(long id, String[] args,
			DistributionState distribStateDistance) throws Exception {
		super(id, args);

		if (args[0].equals(getClass().getName()) == false)
			throw new Exception("Invalid Operation Type");

		this.lon = Double.parseDouble(args[1]);
		this.lat = Double.parseDouble(args[2]);
		this.startGid = Long.parseLong(args[3]);
		this.endGid = Long.parseLong(args[4]);

		this.distribStateDistance = distribStateDistance;
	}

	@Override
	public boolean onExecute(GraphDatabaseService db) {
		boolean result = true;

		Transaction tx = db.beginTx();

		try {

			Node newNode = db.createNode();

			Node startNode = db.getNodeById(startGid);
			Node endNode = db.getNodeById(endGid);

			newNode.setProperty(Consts.LONGITUDE, lon);
			newNode.setProperty(Consts.LATITUDE, lat);

			for (Relationship rel : startNode.getRelationships()) {
				if (rel.getOtherNode(startNode).getId() != endNode.getId())
					continue;

				Relationship newStartRel = newNode.createRelationshipTo(
						startNode, rel.getType());
				newStartRel.setProperty(Consts.WEIGHT, (Double) rel
						.getProperty(Consts.WEIGHT) / 2);

				Relationship newEndRel = newNode.createRelationshipTo(endNode,
						rel.getType());
				newEndRel.setProperty(Consts.WEIGHT, (Double) rel
						.getProperty(Consts.WEIGHT) / 2);

				rel.delete();

			}

			if (distribStateDistance != null) {
				double minDistanceToCityScore = OperationGIS
						.getMinDistanceToCityScore(lon, lat);
				distribStateDistance.sumValues += 1 / minDistanceToCityScore;
				distribStateDistance.values.put(newNode.getId(),
						1 / OperationGIS.getMinDistanceToCityScore(lon, lat));
			}

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			tx.finish();
		}

		return result;
	}
}
