package com.ontapib.cluster.model;

public class Adapter {

	private String adapterName;
	private int slotNumber;
	private String adapterType;
	private boolean isOnboard;
	
	public Adapter() {}

	public String getAdapterName() {
		return adapterName;
	}

	public void setAdapterName(String adapterName) {
		this.adapterName = adapterName;
	}

	public int getSlotNumber() {
		return slotNumber;
	}

	public void setSlotNumber(int slotNumber) {
		this.slotNumber = slotNumber;
	}

	public String getAdapterType() {
		return adapterType;
	}

	public void setAdapterType(String adapterType) {
		this.adapterType = adapterType;
	}

	public boolean isOnboard() {
		return isOnboard;
	}

	public void setOnboard(boolean isOnboard) {
		this.isOnboard = isOnboard;
	}

}
