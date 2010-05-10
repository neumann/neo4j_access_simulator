package sim_tst;

import graph_gen_utils.NeoFromFile;
import graph_gen_utils.general.Consts;
import graph_gen_utils.general.DirUtils;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.EmbeddedGraphDatabase;

import p_graph_service.PGraphDatabaseService;
import p_graph_service.core.InstanceInfo;
import p_graph_service.core.PGraphDatabaseServiceImpl;
import p_graph_service.sim.InfoNode;
import p_graph_service.sim.PGraphDatabaseServiceSIM;

import simulator.OperationFactory;
import simulator.Simulator;
import simulator.gis.LogOperationFactoryGIS;
import simulator.gis.OperationFactoryGIS;
import simulator.gis.SimulatorGIS;
import simulator.gis.astar.GISRelationshipTypes;

public class GISTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// testSmallGraph();

		// String graphType = "NORMAL";
		// GraphDatabaseService db = new EmbeddedGraphDatabase(
		// "var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS");

		String graphType = "INFO";
		GraphDatabaseService db = new PGraphDatabaseServiceSIM(
				"var/gis/romania-BAL2-GID-NAME-COORDS-ALL_RELS", 0);

		// String graphType = "PARTITIONED";
		// String pdbDir =
		// "/home/alex/workspace/neo4j_access_simulator/var/gis/";
		// String pdbStr =
		// "partitioned-romania-BAL2-GID-NAME-COORDS-ALL_RELS";
		// PGraphDatabaseService db = new PGraphDatabaseServiceImpl(pdbDir
		// + pdbStr, 0);

		// double addRatio = 0.25;
		// double delRatio = 0.25;
		// double localRatio = 0.25;
		// double globalRatio = 0.25;
		// long opCount = 1000;
		// OperationFactory operationFactory = new OperationFactoryGIS(db,
		// addRatio, delRatio, localRatio, globalRatio, opCount);

		String inputLogDir = "var/gis/";
		String inputLogFile = "log-gis-romania-INPUT-READ-LOCAL-100.txt";

		OperationFactory operationFactory = new LogOperationFactoryGIS(
				inputLogDir + inputLogFile);

		String outputLogDir = "var/gis/";
		String outputLogFile = String.format("log-gis-romania-OUTPUT-%s.txt",
				graphType);

		Simulator sim = new SimulatorGIS(db, outputLogDir + outputLogFile,
				operationFactory);
		sim.startSIM();

		System.out.println("SLUT");

		db.shutdown();

		// System.out.println(InfoGraphDatabaseService.accessToString());
	}

	private static void testSmallGraph() {
		String dbDir = "var/gis/test";
		DirUtils.cleanDir(dbDir);
		PGraphDatabaseServiceSIM infoNeo = new PGraphDatabaseServiceSIM(dbDir,
				0);

		InfoNode infoNode1 = null;
		InfoNode infoNode2 = null;
		InfoNode infoNode3 = null;
		InfoNode infoNode4 = null;

		Transaction tx = infoNeo.beginTx();
		try {
			infoNode1 = (InfoNode) infoNeo.createNode();
			infoNode1.setProperty(Consts.COLOR, (byte) 0);
			infoNode1.setProperty(Consts.NAME, "infoNode1");

			infoNode2 = (InfoNode) infoNeo.createNode();
			infoNode2.setProperty(Consts.COLOR, (byte) 0);
			infoNode2.setProperty(Consts.NAME, "infoNode2");

			infoNode3 = (InfoNode) infoNeo.createNode();
			infoNode3.setProperty(Consts.COLOR, (byte) 1);
			infoNode3.setProperty(Consts.NAME, "infoNode3");

			infoNode4 = (InfoNode) infoNeo.createNode();
			infoNode4.setProperty(Consts.COLOR, (byte) 1);
			infoNode4.setProperty(Consts.NAME, "infoNode4");

			infoNode1.createRelationshipTo(infoNode2,
					GISRelationshipTypes.CAR_WAY);
			infoNode1.createRelationshipTo(infoNode3,
					GISRelationshipTypes.CAR_WAY);
			infoNode1.createRelationshipTo(infoNode4,
					GISRelationshipTypes.CAR_WAY);

			tx.success();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.finish();
		}

		String beforeInfo = "";
		for (long instanceId : infoNeo.getInstancesIDs()) {
			beforeInfo = String.format("%s%s\n", beforeInfo, infoNeo
					.getInstanceInfoFor(instanceId));
		}
		System.out.printf("BEFORE\n%s", beforeInfo);

		tx = infoNeo.beginTx();
		try {
			for (Relationship infoRel : infoNode1.getRelationships()) {
				// Node otherNode = infoRel.getOtherNode(infoNode1);
				// System.out.printf("Node[%s] Color[%d]\n", otherNode
				// .getProperty(Consts.NAME), otherNode
				// .getProperty(Consts.COLOR));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			tx.finish();
		}

		String afterInfo = "";
		for (long instanceId : infoNeo.getInstancesIDs()) {
			afterInfo = String.format("%s%s\n", afterInfo, infoNeo
					.getInstanceInfoFor(instanceId));
		}
		System.out.printf("AFTER\n%s", afterInfo);

		// info.put(INTERHOP_TAG, dif.getValue(InfoKey.InterHop).toString());
		// info.put(TRAFFIC_TAG, dif.getValue(InfoKey.Traffic).toString());
		// info.put(HOP_TAG, dif.getValue(InfoKey.IntraHop).toString());

		infoNeo.shutdown();
	}

}
