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
	private double budget;
	private String sodSchein;
	private String sodBasePrice;
	private String sodFlexPrice;
	private String sodBaseCapacity;
	private String sodFlexCapacity;

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

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

	public String getSodSchein() {
		return sodSchein;
	}

	public void setSodSchein(String sodSchein) {
		this.sodSchein = sodSchein;
	}

	public String getSodBasePrice() {
		return sodBasePrice;
	}

	public void setSodBasePrice(String sodBasePrice) {
		this.sodBasePrice = sodBasePrice;
	}

	public String getSodFlexPrice() {
		return sodFlexPrice;
	}

	public void setSodFlexPrice(String sodFlexPrice) {
		this.sodFlexPrice = sodFlexPrice;
	}

	public String getSodBaseCapacity() {
		return sodBaseCapacity;
	}

	public void setSodBaseCapacity(String sodBaseCapacity) {
		this.sodBaseCapacity = sodBaseCapacity;
	}

	public String getSodFlexCapacity() {
		return sodFlexCapacity;
	}

	public void setSodFlexCapacity(String sodFlexCapacity) {
		this.sodFlexCapacity = sodFlexCapacity;
	}

	public String toString() {
		return "Cluster [clusterName=" + clusterName + "]";
	}
}
