package com.ontapib.cluster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.service.ClusterService;

@RestController
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@RequestMapping("/create")
	public String createCluster(@RequestParam String clusterName, @RequestParam String version) {
		Cluster c = clusterService.createCluster(clusterName, version);
		return c.toString();
	}

}
