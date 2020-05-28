package com.ontapib.cluster.model;

public class Shelve extends Component {
	private String serialNumber;
	private String model;
	private double diskSize;
	private int diskAmount;

	public Shelve(String serialNumber, String model) {
		super();
		this.serialNumber = serialNumber;
		this.model = model;
	}

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

	public double getDiskSize() {
		return diskSize;
	}

	public void setDiskSize(double diskSize) {
		this.diskSize = diskSize;
	}

	public int getDiskAmount() {
		return diskAmount;
	}

	public void setDiskAmount(int diskAmount) {
		this.diskAmount = diskAmount;
	}

}
