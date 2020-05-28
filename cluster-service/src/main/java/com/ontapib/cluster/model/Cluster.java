package com.ontapib.cluster.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Cluster {

	@Id
	private String Id;
	private String clusterName;
	private String clusterIdentifier;
	private List<Node> nodes;

	public Cluster() {}

	public Cluster(String clusterName, String clusterIdentifier, List<Node> nodes) {
		super();
		this.clusterName = clusterName;
		this.nodes = nodes;
	}

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	public String getClusterIdentifier() {
		return clusterIdentifier;
	}

	public void setClusterIdentifier(String clusterIdentifier) {
		this.clusterIdentifier = clusterIdentifier;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public String toString() {
		return "Cluster [clusterName=" + clusterName + "]";
	}
}
