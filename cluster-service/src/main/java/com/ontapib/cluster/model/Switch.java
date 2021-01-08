package com.ontapib.cluster.model;

public class Switch {
	private String Id;
	private String modelName;
	private String switchName;
	private String switchIp;

	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getSwitchName() {
		return switchName;
	}

	public void setSwitchName(String switchName) {
		this.switchName = switchName;
	}

	public String getSwitchIp() {
		return switchIp;
	}

	public void setSwitchIp(String switchIp) {
		this.switchIp = switchIp;
	}
}
