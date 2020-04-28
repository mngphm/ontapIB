package com.ontapib.cluster.controller;

import java.io.StringReader;

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
import com.ontapib.cluster.service.ClusterService;

@RestController
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/create")
	public String createCluster(@RequestBody Cluster cluster) {
		Cluster c = clusterService.createCluster(cluster.getClusterName(), cluster.getClusterVersion(),
				cluster.getShelves());
		return c.toString();
	}

	@RequestMapping("/import/{clusterSerial}")
	public String importCluster(@PathVariable("clusterSerial") String clusterSerial) {
		String importedCluster = webClientBuilder.build().get().uri(
				"http://reststg.corp.netapp.com/asup-rest-interface/ASUP_DATA/client_id/sc_inventory/sys_serial_no/"
						+ clusterSerial)
				.exchange().block().bodyToMono(String.class).block();
		
		Cluster newCluster = new Cluster();
		try {
			SAXParserFactory fact = SAXParserFactory.newInstance();
			SAXParser saxParser = fact.newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				boolean bclusterName = false;
				boolean bserial = false;
				boolean bmodelName = false;
				boolean bclusterVersion = false;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element: " + qName);
					if (qName.equals("sys_serial_no"))
						bserial = true;
					if (qName.equals("hostname"))
						bclusterName = true;
					if (qName.equals("sys_model"))
						bserial = true;
					if (qName.equals("sys_version"))
						bclusterName = true;
				}

				public void endElement(String uri, String localName, String qName) {
					System.out.println("End Element: " + qName);
				}

				public void characters(char[] ch, int start, int length) throws SAXException {
					if (bserial) {
						String serialnumber = new String(ch, start, length);
						System.out.println("Serial: " + serialnumber);
						newCluster.setSerialnumber(serialnumber);
						bserial = false;
					}
					if (bclusterName) {
						String clusterName = new String(ch, start, length);
						System.out.println("Clustername: " + clusterName);
						newCluster.setClusterName(clusterName);
						bclusterName = false;
					}
					if (bclusterVersion) {
						String clusterVersion = new String(ch, start, length);
						System.out.println("Clusterversion: " + clusterVersion);
						newCluster.setClusterVersion(clusterVersion);
						bclusterName = false;
					}
					if (bmodelName) {
						String model = new String(ch, start, length);
						System.out.println("Model: " + model);
						newCluster.setModel(model);
						bclusterName = false;
					}
				}
			};
			
			saxParser.parse(new InputSource(new StringReader(importedCluster)), handler);
			

		} catch (Exception e) {
			// TODO: handle exception
		}

		return importedCluster;

	}
}
