package com.ontapib.cluster.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Node {
	@Id
	private String Id;
	private String nodeName;
	private String serialnumber;
	private String model;
	private Date warrantyEndDate;
	private String version;
	private List<Shelve> shelves;

	public Node() {
	}

	public Node(String nodeName, String version, String serialnumber, String model, 
			Date warrantyEndDate, List<Shelve> shelves) {
		super();
		this.nodeName = nodeName;
		this.version = version;
		this.serialnumber = serialnumber;
		this.model = model;
		this.warrantyEndDate = warrantyEndDate;
		this.shelves = shelves;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getSerialnumber() {
		return serialnumber;
	}

	public void setSerialnumber(String serialnumber) {
		this.serialnumber = serialnumber;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
