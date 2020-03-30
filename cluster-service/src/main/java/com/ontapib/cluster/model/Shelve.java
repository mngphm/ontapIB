package com.ontapib.cluster.model;

public class Shelve {

	public Shelve(String serialNumber, String model) {
		super();
		this.serialNumber = serialNumber;
		this.model = model;
	}

	private String serialNumber;
	private String model;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
