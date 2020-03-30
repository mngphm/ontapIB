package com.ontapib.cluster.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Shelve;
import com.ontapib.cluster.service.ClusterService;

@RestController
public class ClusterController {

	@Autowired
	private ClusterService clusterService;

	@RequestMapping("/create")
	public String createCluster(@RequestBody String clusterName, @RequestBody String version,
			@RequestBody List<Shelve> shelves) {
		Cluster c = clusterService.createCluster(clusterName, version, shelves);
		return c.toString();
	}
}
