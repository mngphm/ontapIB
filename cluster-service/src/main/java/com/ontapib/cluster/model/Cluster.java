package com.ontapib.cluster.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Cluster {

	public Cluster(String clusterName, String clusterVersion) {
		this.clusterName = clusterName;
		this.clusterVersion = clusterVersion;
	}
	
	@Id
	private String Id;
	private String clusterName;
	private String clusterVersion;

	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getClusterVersion() {
		return clusterVersion;
	}

	public void setClusterVersion(String clusterVersion) {
		this.clusterVersion = clusterVersion;
	}

	public String toString() {
		return "Cluster [clusterName=" + clusterName + ", clusterVersion=" + clusterVersion + "]";
	}
}
