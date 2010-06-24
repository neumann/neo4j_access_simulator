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
	// -> 0 id
	// -> 1 type
	// -> 2 lon
	// -> 3 lan
	// -> 4 startGid
	// -> 5 endGid
	public OperationGISAddNode(String[] args) {
		super(args);

		this.lon = Double.parseDouble(args[2]);
		this.lat = Double.parseDouble(args[3]);
		this.startGid = Long.parseLong(args[4]);
		this.endGid = Long.parseLong(args[5]);
	}

	public OperationGISAddNode(String[] args,
			DistributionState distribStateDistance) {
		super(args);

		this.lon = Double.parseDouble(args[2]);
		this.lat = Double.parseDouble(args[3]);
		this.startGid = Long.parseLong(args[4]);
		this.endGid = Long.parseLong(args[5]);

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
			newNode.setProperty(Consts.NODE_GID, newNode.getId());

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

			appendToLog(Long.toString(newNode.getId()));

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
