package com.ontapib.cluster.controller;

import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Node;
import com.ontapib.cluster.service.ClusterService;
import com.ontapib.cluster.service.NodeService;

@RestController
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@Autowired
	private NodeService nodeService;
	
	@Autowired
	private WebClient.Builder webClientBuilder;

	@RequestMapping("/cluster/create")
	public String createCluster(@RequestBody Cluster cluster) {
		Cluster c = clusterService.createCluster(cluster.getClusterName(), cluster.getClusterIdentifier(),
				cluster.getNodes());
		return c.toString();
	}
	
	@RequestMapping("/cluster/clusters")
	public List<Cluster> getAllClusters() {
		return clusterService.getAllClusters();
	}

	public List<Node> importNodes(Cluster c, String clusterIdentifier) {
		List<Node> nodeList = new ArrayList<>();

		String getASUPnode = webClientBuilder.build().get().uri(
				"http://reststg.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/cluster_identifier/"
						+ clusterIdentifier)
				.exchange().block().bodyToMono(String.class).block();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				Node newNode = null;
				
				boolean bnodeName = false;
				boolean bnodeSerial = false;
				boolean bmodelName = false;
				boolean bnodeVersion = false;
				boolean bwarrantyEndDate = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element: " + qName);
					if (qName.equals("system")) {
						newNode = new Node();
					}
					if (qName.equals("hostname"))
						bnodeName = true;
					if (qName.equals("sys_serial_no"))
						bnodeSerial = true;
					if (qName.equals("sys_model"))
						bmodelName = true;
					if (qName.equals("sys_version"))
						bnodeVersion = true;
					if (qName.equals("warranty_end_date"))
						bwarrantyEndDate = true;
				}

				public void endElement(String uri, String localName, String qName) {
					System.out.println("End Element: " + qName);
					if(qName.equalsIgnoreCase("system")) {
						nodeList.add(newNode);
					}
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (bnodeName) {
						String nodeName = new String(ch, start, length);
						System.out.println("NodeName: " + nodeName);
						newNode.setNodeName(nodeName);
						bnodeName = false;
					}

					if (bnodeSerial) {
						String serialnumber = new String(ch, start, length);
						System.out.println("Serial: " + serialnumber);
						newNode.setSerialnumber(serialnumber);
						bnodeSerial = false;
					}

					if (bnodeVersion) {
						String nodeVersion = new String(ch, start, length);
						System.out.println("NodeVersion: " + nodeVersion);
						newNode.setVersion(nodeVersion);
						bnodeVersion = false;
					}
					if (bmodelName) {
						String model = new String(ch, start, length);
						System.out.println("Model: " + model);
						newNode.setModel(model);
						bmodelName = false;
					}
					if (bwarrantyEndDate) {
						String warrantyEndDate = new String(ch, start, length);
						System.out.println("Warranty End Date: " + warrantyEndDate);
						try {
							newNode.setWarrantyEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(warrantyEndDate));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						bwarrantyEndDate = false;
					}
				}
			};
			

			saxParser.parse(new InputSource(new StringReader(getASUPnode)), handler);
			for (Node node : nodeList) {
				node.setCluster(c);
				nodeService.createNode(node);
			}


		} catch (Exception e) {
			// TODO: handle exception
		}

		return nodeList;

	}

	@RequestMapping("/node/import/{nodeSerial}")
	public String importNode(@PathVariable("nodeSerial") String nodeSerial) {
		List<Node> nodeList = null;
		String asupCluster = webClientBuilder.build().get().uri(
				"http://reststg.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/sys_serial_no/"
						+ nodeSerial)
				.exchange().block().bodyToMono(String.class).block();

		Cluster c = new Cluster();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				boolean bclusterIdentifier = false;
				boolean bclusterName = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element: " + qName);
					if (qName.equals("cluster_identifier"))
						bclusterIdentifier = true;
					if (qName.equals("cluster_name"))
						bclusterName = true;

				}

				public void endElement(String uri, String localName, String qName) {
					System.out.println("End Element: " + qName);
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (bclusterIdentifier) {
						String clusterIdentifier = new String(ch, start, length);
						c.setClusterIdentifier(clusterIdentifier);
						System.out.println("ClusterIdentifier: " + clusterIdentifier);
						bclusterIdentifier = false;
					}
					if (bclusterName) {
						String clusterName = new String(ch, start, length);
						c.setClusterName(clusterName);
						System.out.println("ClusterIdentifier: " + clusterName);
						bclusterName = false;
					}
				}
			};

			saxParser.parse(new InputSource(new StringReader(asupCluster)), handler);

			nodeList = importNodes(c, c.getClusterIdentifier());
			c.setNodes(nodeList);
			clusterService.createCluster(c);

			

		} catch (Exception e) {
			// TODO: handle exception
		}
		return asupCluster;
	}
}
