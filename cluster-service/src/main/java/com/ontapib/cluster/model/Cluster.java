package com.ontapib.cluster.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Cluster {

	public Cluster() {}

	public Cluster(String serialnumber, String clusterName, String clusterVersion, String model, Date warrantyEndDate,
			List<Shelve> shelves) {
		super();
		this.serialnumber = serialnumber;
		this.clusterName = clusterName;
		this.clusterVersion = clusterVersion;
		this.model = model;
		this.warrantyEndDate = warrantyEndDate;
		this.shelves = shelves;
	}

	@Id
	private String Id;
	private String serialnumber;
	private String clusterName;
	private String clusterVersion;
	private String model;
	private Date warrantyEndDate;
	private List<Shelve> shelves;
	
	public String getSerialnumber() {
		return serialnumber;
	}
	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
	}
	
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public Date getWarrantyEndDate() {
		return warrantyEndDate;
	}

	public void setWarrantyEndDate(Date warrantyEndDate) {
		this.warrantyEndDate = warrantyEndDate;
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
