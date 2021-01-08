package com.ontapib.cluster.controller;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.gson.Gson;
import com.ontapib.cluster.model.Aggregate;
import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Component;
import com.ontapib.cluster.model.Node;
import com.ontapib.cluster.model.ResponseJSONSystemContract;
import com.ontapib.cluster.model.Switch;
import com.ontapib.cluster.restclient.NetappClient;
import com.ontapib.cluster.service.NodeService;

@RestController
public class NodeController {

	@Autowired
	private WebClient.Builder webClientBuilder;

	@Autowired
	private NodeService nodeService;

	@Autowired
	private NetappClient netappClient;

	@RequestMapping("/node/nodes")
	public List<Node> getAllNodes() {
		return nodeService.getAllNodes();
	}

	@RequestMapping("/node/{nodeSerial}")
	public Node getNode(@PathVariable("nodeSerial") String nodeSerial) {
		return nodeService.getNode(nodeSerial);
	}

	@RequestMapping("/node/import/{nodeSerial}")
	public List<Component> importNode(@PathVariable("nodeSerial") String nodeSerial) {
		List<Component> importedNodes = null;

		if (nodeService.getNode(nodeSerial) != null)
			return null;

		Cluster c = netappClient.getCluster(nodeSerial);

		importedNodes = importClusterNodeMembers(c, c.getClusterIdentifier());

		return importedNodes;
	}

	@RequestMapping("/node/updateContracts/{nodeSerial}")
	public String updateNodeContract(@PathVariable("nodeSerial") String nodeSerial) {
		Node node = nodeService.getNode(nodeSerial);
		node.setWarrantyEndDate(netappClient.getNodeContractEndDate(nodeSerial));
		nodeService.setNode(node);
		return node.toString();
	}

	@RequestMapping("/node/updateaggr/{nodeSerial}")
	public String updateNodeAggregates(@PathVariable("nodeSerial") String nodeSerial) {
		Node node = nodeService.getNode(nodeSerial);
		List<Aggregate> aggrList = new ArrayList<>();
		double usedSpace = 0;
		double allocatedSpace = 0;
		double availSpace = 0;
		double usedPercentage = 0;

		aggrList = netappClient.getNodeAggregates(node.getAsupBizkey());

		for (Aggregate aggregate : aggrList) {
			if (!aggregate.isRoot()) {
				usedSpace = usedSpace + aggregate.getAggrUsed();
				allocatedSpace = allocatedSpace + aggregate.getAggrUsable();
				availSpace = availSpace + aggregate.getAggrAvail();
			}
		}

		usedPercentage = (usedSpace / allocatedSpace);

		node.setAllocatedSpace(allocatedSpace);
		node.setUsedSpace(usedSpace);
		node.setAvailSpace(availSpace);
		node.setUsedPct(usedPercentage);
		node.setAggregates(aggrList);
		nodeService.setNode(node);

		return node.toString();
	}

	@RequestMapping("/node/updateAdapterCards/{nodeSerial}")
	public Node updateAdapterCards(@PathVariable("nodeSerial") String nodeSerial) {
		Node node = nodeService.getNode(nodeSerial);

//		String getASUPNodeInfo = webClientBuilder.build().get().uri(
//				"http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/biz_key/"
//						+ node.getAsupBizkey() + "/object_data/object/adapter/")
//				.retrieve().bodyToMono(String.class).block();

		return null;
	}

	@RequestMapping("/node/updateShelves/{nodeSerial}")
	public Node updateShelves() {
		return null;
	}

	public List<Component> importClusterNodeMembers(Cluster c, String clusterIdentifier) {
		List<Component> nodeList = new ArrayList<>();
		List<Switch> switchList = new ArrayList<>();

		c.setSwitches(netappClient.getClusterSwitches(clusterIdentifier));

		nodeList = netappClient.getClusterNodes(clusterIdentifier);

		for (Component node : nodeList) {

			/*
			 * Set node cluster
			 */
			((Node) node).setCluster(c);

			/*
			 * Set node warranty end date
			 */
			((Node) node).setWarrantyEndDate(netappClient.getNodeContractEndDate(((Node) node).getSerialnumber()));

			/*
			 * Set node Aggregates and total capacity info
			 */
			List<Aggregate> aggrList = netappClient.getNodeAggregates(((Node) node).getAsupBizkey());
			double usedSpace = 0;
			double allocatedSpace = 0;
			double availSpace = 0;
			double usedPercentage = 0;

			for (Aggregate aggregate : aggrList) {
				if (!aggregate.isRoot()) {
					usedSpace = usedSpace + aggregate.getAggrUsed();
					allocatedSpace = allocatedSpace + aggregate.getAggrUsable();
					availSpace = availSpace + aggregate.getAggrAvail();
				}
			}

			usedPercentage = (usedSpace / allocatedSpace);

			((Node) node).setAllocatedSpace(allocatedSpace);
			((Node) node).setUsedSpace(usedSpace);
			((Node) node).setAvailSpace(availSpace);
			((Node) node).setUsedPct(usedPercentage);
			((Node) node).setAggregates(aggrList);

			System.out.println("Write Node to the DB!");
			nodeService.createNode((Node) node);
		}

		return nodeList;
	}

//	@RequestMapping("/node/createImportedNode")
//	public void createImportedNodes(@RequestBody String importedNodeXml) {
//
//		Cluster c = new Cluster();
//		Node newNode = new Node();
//		
//		try {
//			SAXParserFactory fact = SAXParserFactory.newInstance();
//			SAXParser saxParser = fact.newSAXParser();
//			DefaultHandler handler = new DefaultHandler() {
//
//				boolean bnodeName = false;
//				boolean bnodeSerial = false;
//				boolean bmodelName = false;
//				boolean bnodeVersion = false;
//				boolean bASUP = false;
//
//				public void startElement(String uri, String localName, String qName, Attributes attributes)
//						throws SAXException {
//
//					System.out.println("Start Element: " + qName);
//					if (qName.equals("hostname"))
//						bnodeName = true;
//					if (qName.equals("sys_serial_no"))
//						bnodeSerial = true;
//					if (qName.equals("sys_model"))
//						bmodelName = true;
//					if (qName.equals("sys_version"))
//						bnodeVersion = true;
//					if (qName.equals("biz_key"))
//						bASUP = true;
//
//				}
//
//				public void endElement(String uri, String localName, String qName) {
//					System.out.println("End Element: " + qName);
//				}
//
//				public void characters(char[] ch, int start, int length) throws SAXException {
//					if (bnodeName) {
//						String nodeName = new String(ch, start, length);
//						System.out.println("NodeName: " + nodeName);
//						newNode.setNodeName(nodeName);
//						bnodeName = false;
//					}
//
//					if (bnodeSerial) {
//						String serialnumber = new String(ch, start, length);
//						System.out.println("Serial: " + serialnumber);
//						newNode.setSerialnumber(serialnumber);
//						bnodeSerial = false;
//					}
//
//					if (bnodeVersion) {
//						String nodeVersion = new String(ch, start, length);
//						System.out.println("NodeVersion: " + nodeVersion);
//						newNode.setVersion(nodeVersion);
//						bnodeVersion = false;
//					}
//					if (bmodelName) {
//						String model = new String(ch, start, length);
//						System.out.println("Model: " + model);
//						newNode.setModel(model);
//						bmodelName = false;
//					}
//					if (bASUP) {
//						String bizKey = new String(ch, start, length);
//						System.out.println("ASUP bizkey: " + bizKey);
//						if (newNode.getAsupBizkey() == null) {
//							newNode.setAsupBizkey(bizKey);
//							bASUP = false;
//						}
//						bASUP = false;
//					}
//				}
//			};
//
//			saxParser.parse(new InputSource(new StringReader(importedNodeXml)), handler);
//			nodeService.createNode((Node) newNode);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	}

	public NodeController() {
		// TODO Auto-generated constructor stub
	}

}
