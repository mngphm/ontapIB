package com.ontapib.cluster.restclient;

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
import org.springframework.stereotype.Service;
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

@Service
public class NetappClient {

	@Autowired
	private WebClient.Builder webClientBuilder;

	public Date getNodeContractEndDate(String nodeSerial) {
		Date formattedContractEndDate = null;
		String contractEndDate = "";
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.YEAR, 2);

		Date currentDatePlusTwoYears = c.getTime();

		String getNodeContracts = webClientBuilder.build().get().uri(
				"http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/sys_serial_no/"
						+ nodeSerial + "/contracts/startdate/" + dateFormatter.format(currentDate) + "/enddate/"
						+ dateFormatter.format(currentDatePlusTwoYears))
				.retrieve().bodyToMono(String.class).block();

		System.out.println(getNodeContracts);

		ResponseJSONSystemContract rs = new Gson().fromJson(getNodeContracts, ResponseJSONSystemContract.class);
		contractEndDate = rs.getResults().getSystems().getSystem().get(0).getHw_contract_end_date();
		try {
			formattedContractEndDate = new SimpleDateFormat("EEE MMM d hh:mm:ss zzz yyyy").parse(contractEndDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return formattedContractEndDate;
	}

	public List<Aggregate> getNodeAggregates(String bizKey) {
		List<Aggregate> aggrList = new ArrayList<>();
		double usedSpace = 0;
		double allocatedSpace = 0;
		double availSpace = 0;
		double usedPercentage = 0;

		String asupData = webClientBuilder.build().get()
				.uri("http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/biz_key/"
						+ bizKey + "/object_view/aggregate")
				.retrieve().bodyToMono(String.class).block();

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
						if (aggrTagOpened == aggrTagClosed) {
							aggrList.add(aggr);
							aggrTagOpened = 0;
							aggrTagClosed = 0;
						}
					}
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (baggrRowTag) {
						System.out.println("Aggr opened count: " + aggrTagOpened + " " + aggrTagClosed);
						if (aggrTagOpened == aggrTagClosed) {
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
				if (!aggregate.isRoot()) {
					usedSpace = usedSpace + aggregate.getAggrUsed();
					allocatedSpace = allocatedSpace + aggregate.getAggrUsable();
					availSpace = availSpace + aggregate.getAggrAvail();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return aggrList;
	}

	public List<Switch> getClusterSwitches(String clusterIdentifier) {
		List<Switch> clusterSwitches = new ArrayList<>();
		String getClusterSwitchInfo = webClientBuilder.build().get().uri(
				"http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/cluster_identifier/"
						+ clusterIdentifier + "/object_data/object/cluster_switch")
				.retrieve().bodyToMono(String.class).block();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();

			DefaultHandler handlerForClusterSwitch = new DefaultHandler() {
				Switch newSwitch = null;
				boolean bswitchName = false;
				boolean bswitchModel = false;
				boolean bswitchIp = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					if (qName.equals("cluster_switch"))
						newSwitch = new Switch();
					if (qName.equals("switch_name"))
						bswitchName = true;
					if (qName.equals("switch_model"))
						bswitchModel = true;
					if (qName.equals("switch_ip_address"))
						bswitchIp = true;
				}

				public void endElement(String uri, String localName, String qName) {
					if (qName.equalsIgnoreCase("cluster_switch")) {
						clusterSwitches.add(newSwitch);
					}
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (bswitchName) {
						String switchName = new String(ch, start, length);
						System.out.println("Switch Name: " + switchName);
						newSwitch.setSwitchName(switchName);
						bswitchName = false;
					}
					if (bswitchModel) {
						String switchModel = new String(ch, start, length);
						System.out.println("Switch Model: " + switchModel);
						newSwitch.setModelName(switchModel);
						bswitchModel = false;
					}
					if (bswitchIp) {
						String switchIp = new String(ch, start, length);
						System.out.println("Switch IP: " + switchIp);
						newSwitch.setSwitchIp(switchIp);
						bswitchIp = false;
					}
				}
			};

			saxParser.parse(new InputSource(new StringReader(getClusterSwitchInfo)), handlerForClusterSwitch);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return clusterSwitches;
	}

	public List<Component> getClusterNodes(String clusterIdentifier) {
		List<Component> nodeList = new ArrayList<>();

		String getASUPNodeInfo = webClientBuilder.build().get().uri(
				"http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/cluster_identifier/"
						+ clusterIdentifier + "/system_state/active/limit/1")
				.retrieve().bodyToMono(String.class).block();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				Node newNode = null;

				boolean bnodeName = false;
				boolean bnodeSerial = false;
				boolean bnodeSystemId = false;
				boolean bmodelName = false;
				boolean bnodeVersion = false;
				boolean bASUP = false;
				boolean bPartnerNodeName = false;
				boolean bPartherSystemId = false;
				boolean bSiteName = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					if (qName.equals("system"))
						newNode = new Node();
					if (qName.equals("hostname"))
						bnodeName = true;
					if (qName.equals("sys_serial_no"))
						bnodeSerial = true;
					if (qName.equals("system_id"))
						bnodeSystemId = true;
					if (qName.equals("sys_model"))
						bmodelName = true;
					if (qName.equals("sys_version"))
						bnodeVersion = true;
					if (qName.equals("biz_key"))
						bASUP = true;
					if (qName.equals("partner_hostname"))
						bPartnerNodeName = true;
					if (qName.equals("partner_system_id"))
						bPartherSystemId = true;
					if (qName.equals("site_name"))
						bSiteName = true;
				}

				public void endElement(String uri, String localName, String qName) {
//					System.out.println("End Element: " + qName);
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

					if (bnodeSystemId) {
						String systemId = new String(ch, start, length);
						System.out.println("SystemId: " + systemId);
						newNode.setSystemId(systemId);
						bnodeSystemId = false;
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
					if (bASUP) {
						String bizKey = new String(ch, start, length);
						System.out.println("ASUP bizkey: " + bizKey);
						if (newNode.getAsupBizkey() == null) {
							newNode.setAsupBizkey(bizKey);
							bASUP = false;
						}
						bASUP = false;
					}

					if (bPartnerNodeName) {
						String partnerNodeName = new String(ch, start, length);
						System.out.println("Partner Nodename: " + partnerNodeName);
						if (newNode.getPartnerNodeName() == null) {
							newNode.setPartnerNodeName(partnerNodeName);
							bPartnerNodeName = false;
						}
						bPartnerNodeName = false;
					}

					if (bPartherSystemId) {
						String partnerSystemId = new String(ch, start, length);
						System.out.println("ASUP bizkey: " + partnerSystemId);
						if (newNode.getPartnerSystemId() == null) {
							newNode.setPartnerSystemId(partnerSystemId);
							bPartherSystemId = false;
						}
						bPartherSystemId = false;
					}

					if (bSiteName) {
						String siteName = new String(ch, start, length);
						System.out.println("ASUP bizkey: " + siteName);
						if (newNode.getSiteName() == null) {
							newNode.setSiteName(siteName);
							bSiteName = false;
						}
						bSiteName = false;
					}
				}
			};

			saxParser.parse(new InputSource(new StringReader(getASUPNodeInfo)), handler);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return nodeList;
	}

	public Cluster getCluster(String nodeSerial) {
		Cluster cluster = new Cluster();
		String request = webClientBuilder.build().get().uri(
				"http://restprd.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/sys_serial_no/"
						+ nodeSerial)
				.retrieve().bodyToMono(String.class).block();

		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				boolean bclusterIdentifier = false;
				boolean bclusterName = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					if (qName.equals("cluster_identifier"))
						bclusterIdentifier = true;
					if (qName.equals("cluster_name"))
						bclusterName = true;

				}

				public void endElement(String uri, String localName, String qName) {
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (bclusterIdentifier) {
						String clusterIdentifier = new String(ch, start, length);
						cluster.setClusterIdentifier(clusterIdentifier);
						System.out.println("ClusterIdentifier: " + clusterIdentifier);
						bclusterIdentifier = false;
					}
					if (bclusterName) {
						String clusterName = new String(ch, start, length);
						cluster.setClusterName(clusterName);
						System.out.println("ClusterIdentifier: " + clusterName);
						bclusterName = false;
					}
				}
			};

			saxParser.parse(new InputSource(new StringReader(request)), handler);

		} catch (Exception e) {
			// TODO: handle exception
		}

		return cluster;
	}
}
