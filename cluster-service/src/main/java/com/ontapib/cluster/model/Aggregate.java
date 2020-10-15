package com.ontapib.cluster.model;

public class Aggregate extends Component {
	
	private String aggrName;
	private double aggrUsable;
	private double aggrUsed;
	private double aggrAvail;
	private double aggrUsedPct;

	public Aggregate() {
	}

	public String getAggrName() {
		return aggrName;
	}

	public void setAggrName(String aggrName) {
		this.aggrName = aggrName;
	}

	public double getAggrUsable() {
		return aggrUsable;
	}

	public void setAggrUsable(double aggrUsable) {
		this.aggrUsable = aggrUsable;
	}

	public double getAggrUsed() {
		return aggrUsed;
	}

	public void setAggrUsed(double aggrUsed) {
		this.aggrUsed = aggrUsed;
	}

	public double getAggrAvail() {
		return aggrAvail;
	}

	public void setAggrAvail(double aggrAvail) {
		this.aggrAvail = aggrAvail;
	}

	public double getAggrUsedPct() {
		return aggrUsedPct;
	}

	public void setAggrUsedPct(double aggrUsedPct) {
		this.aggrUsedPct = aggrUsedPct;
	}

}
