package com.ontapib.cluster.controller;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

import com.ontapib.cluster.model.Aggregate;
import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Component;
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

	public List<Component> importNodes(Cluster c, String clusterIdentifier) {
		List<Component> nodeList = new ArrayList<>();

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
				boolean bASUP = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element: " + qName);
					if (qName.equals("system"))
						newNode = new Node();
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
					if (qName.equals("biz_key"))
						bASUP = true;
				}

				public void endElement(String uri, String localName, String qName) {
					System.out.println("End Element: " + qName);
					if (qName.equalsIgnoreCase("system")) {
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
					if (bASUP) {
						String bizKey = new String(ch, start, length);
						System.out.println("ASUP bizkey: " + bizKey);
						if (newNode.getAsupBizkey() == null) {
							newNode.setAsupBizkey(bizKey);
							bASUP = false;
						}
						bASUP = false;
					}
				}
			};

			saxParser.parse(new InputSource(new StringReader(getASUPnode)), handler);
			for (Component node : nodeList) {
				((Node) node).setCluster(c);
				System.out.println("Write Node to the DB!");
				nodeService.createNode((Node) node);
				updateAsupNode(((Node) node).getSerialnumber());
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return nodeList;
	}

	@RequestMapping("/node/updateASUP/{nodeSerial}")
	public String updateAsupNode(@PathVariable("nodeSerial") String nodeSerial) {
		Node node = nodeService.getNode(nodeSerial);
		List<Aggregate> aggrList = new ArrayList<>();
		double usedSpace = 0;
		double allocatedSpace = 0;
		double availSpace = 0;
		double usedPercentage = 0;

		String asupData = webClientBuilder.build().get()
				.uri("http://reststg.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/biz_key/"
						+ node.getAsupBizkey() + "/object_view/aggregate")
				.exchange().block().bodyToMono(String.class).block();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {

				Aggregate aggr = null;
				int aggrTagOpened = 0;
				int aggrTagClosed = 0;
				boolean baggrRowTag = false;
				boolean baggrName = false;
				boolean baggrUsed = false;
				boolean baggrUsable = false;
				boolean baggrUsedPct = false;
				boolean baggrIsRoot = false;
				boolean baggrAvail = false;
				
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element: " + qName);
					if (qName.equals("row"))
						baggrRowTag = true;
					if (qName.equals("aggr_name"))
						baggrName = true;
					if (qName.equals("aggr_used_kb"))
						baggrUsed = true;
					if (qName.equals("aggr_allocated_kb"))
						baggrUsable = true;
					if (qName.equals("aggr_used_pct"))
						baggrUsedPct = true;
					if (qName.equals("aggr_is_root"))
						baggrIsRoot = true;
					if (qName.equals("aggr_avail_kb"))
						baggrAvail = true;
					
				}

				public void endElement(String uri, String localName, String qName) {
					System.out.println("End Element: " + qName);
					if (qName.equalsIgnoreCase("row")) {
						System.out.println("Aggr opened and closed count: " + aggrTagOpened + " " + aggrTagClosed);
						aggrTagClosed++;
						if(aggrTagOpened == aggrTagClosed) {
							aggrList.add(aggr);
							aggrTagOpened = 0;
							aggrTagClosed = 0;
						}
					}
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (baggrRowTag) {
						System.out.println("Aggr opened count: " + aggrTagOpened + " " + aggrTagClosed);
						if(aggrTagOpened == aggrTagClosed) {
							aggr = new Aggregate();
						}
						aggrTagOpened++;
						baggrRowTag = false;
					}
					if (baggrName) {
						String aggrName = new String(ch, start, length);
						aggr.setAggrName(aggrName);
						System.out.println("Aggregate Name: " + aggrName);
						baggrName = false;
					}
					if (baggrUsed) {
						String aggrUsed = new String(ch, start, length);
						aggr.setAggrUsed(Double.parseDouble(aggrUsed));
						System.out.println("Aggregate Used: " + aggrUsed);
						baggrUsed = false;
					}
					if (baggrUsable) {
						String aggrUsable = new String(ch, start, length);
						aggr.setAggrUsable(Double.parseDouble(aggrUsable));
						System.out.println("Aggregate Usable: " + aggrUsable);
						baggrUsable = false;
					}
					if (baggrAvail) {
						String aggrAvail = new String(ch, start, length);
						aggr.setAggrAvail(Double.parseDouble(aggrAvail));
						System.out.println("Aggregate Available: " + aggrAvail);
						baggrAvail = false;
					}
					if (baggrUsedPct) {
						String aggrUsedPct = new String(ch, start, length);
						aggr.setAggrUsedPct(Double.parseDouble(aggrUsedPct));
						System.out.println("Aggregate Used Percentage: " + aggrUsedPct);
						baggrUsedPct = false;
					}
					if (baggrIsRoot) {
						String aggrIsRoot = new String(ch, start, length);
						aggr.setRoot(Boolean.parseBoolean(aggrIsRoot));
						System.out.println("Is root aggregate: " + aggrIsRoot);
						baggrIsRoot = false;
					}
					
				}
			};
			
			saxParser.parse(new InputSource(new StringReader(asupData)), handler);
			for (Aggregate aggregate : aggrList) {
				if(!aggregate.isRoot()) {
					usedSpace = usedSpace + aggregate.getAggrUsed();
					allocatedSpace = allocatedSpace + aggregate.getAggrUsable();
					availSpace = availSpace + aggregate.getAggrAvail();
				}
			}

			usedPercentage = (availSpace / allocatedSpace);
			
			node.setAllocatedSpace(allocatedSpace);
			node.setUsedSpace(usedSpace);
			node.setAvailSpace(availSpace);
			node.setUsedPct(usedPercentage);
			node.setAggregates(aggrList);
			nodeService.setNode(node);

		} catch (Exception e) {
			// TODO: handle exception
		}
		return asupData;
	}

	@RequestMapping("/node/import/{nodeSerial}")
	public String importNode(@PathVariable("nodeSerial") String nodeSerial) {
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

			importNodes(c, c.getClusterIdentifier());

		} catch (Exception e) {
			// TODO: handle exception
		}
		return asupCluster;
	}
}
