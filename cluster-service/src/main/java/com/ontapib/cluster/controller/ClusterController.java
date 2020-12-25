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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.ontapib.cluster.service.ClusterService;
import com.ontapib.cluster.service.NodeService;

@RestController
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

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
}
