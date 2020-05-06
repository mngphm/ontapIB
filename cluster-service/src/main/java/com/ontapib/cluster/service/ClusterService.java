package com.ontapib.cluster.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontapib.cluster.model.Cluster;
import com.ontapib.cluster.model.Node;
import com.ontapib.cluster.repositroy.ClusterRepository;

@Service
public class ClusterService {

	@Autowired
	private ClusterRepository clusterRepository;

	// Create Cluster
	public Cluster createCluster(String clusterName, String clusterIdentifier, 
			List<Node> nodes) {
		return clusterRepository.save(new Cluster(clusterName, clusterIdentifier, nodes));
	}
	
	public Cluster createCluster(Cluster cluster) {
		return clusterRepository.save(cluster);
	}
	
	// Get Clusterlist
	public List<Cluster> getAllClusters() {
		return clusterRepository.findAll();
	}
}
