package com.ontapib.cluster.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Sod {

	private String sodNumber;
	private double sodCapacity;
	private double sodBaseline;
	private double sodFlexCapacity;
	private Date sodEndDate;
	private List<Component> components;

	public Sod(String sodNumber, double sodBaseline, double sodFlexCapacity, Date sodEndDate,
			List<Component> components) {
		super();
		this.sodNumber = sodNumber;
		this.sodBaseline = sodBaseline;
		this.sodFlexCapacity = sodFlexCapacity;
		this.sodEndDate = sodEndDate;
		this.components = components;
	}

	public Sod() {
		// TODO Auto-generated constructor stub
	}
	
	public String getSodNumber() {
		return sodNumber;
	}

	public void setSodNumber(String sodNumber) {
		this.sodNumber = sodNumber;
	}

	public double getSodCapacity() {
		return sodCapacity;
	}

	public void setSodCapacity(double sodCapacity) {
		this.sodCapacity = sodCapacity;
	}

	public double getSodBaseline() {
		return sodBaseline;
	}

	public void setSodBaseline(double sodBaseline) {
		this.sodBaseline = sodBaseline;
	}

	public double getSodFlexCapacity() {
		return sodFlexCapacity;
	}

	public void setSodFlexCapacity(double sodFlexCapacity) {
		this.sodFlexCapacity = sodFlexCapacity;
	}

	public Date getSodEndDate() {
		return sodEndDate;
	}

	public void setSodEndDate(Date sodEndDate) {
		this.sodEndDate = sodEndDate;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}
}
