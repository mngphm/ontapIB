package com.ontapib.cluster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Shelve;
import com.ontapib.cluster.repositroy.ClusterRepository;

@Service
public class ClusterService {

	@Autowired
	private ClusterRepository clusterRepository;

	// Create Cluster
	public Cluster createCluster(String clusterName, String version, List<Shelve> shelves) {
		return clusterRepository.save(new Cluster(clusterName, version, shelves));
	}

	// Get Clusterlist
	public List<Cluster> getAllClusters() {
		return clusterRepository.findAll();
	}
}
