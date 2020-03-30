package com.ontapib.cluster.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Cluster {

	public Cluster(String clusterName, String clusterVersion, List<Shelve> shelves) {
		this.clusterName = clusterName;
		this.clusterVersion = clusterVersion;
		this.shelves = shelves; 
	}
	
	@Id
	private String Id;
	private String clusterName;
	private String clusterVersion;
	private List<Shelve> shelves;

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

	public List<Shelve> getShelves() {
		return shelves;
	}

	public void setShelves(List<Shelve> shelves) {
		this.shelves = shelves;
	}

	public String toString() {
		return "Cluster [clusterName=" + clusterName + ", clusterVersion=" + clusterVersion + "]";
	}
}
