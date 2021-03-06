package com.ontapib.cluster.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Node extends Component {

	private String nodeName;
	private String serialNumber;
	private String systemId;
	private String model;
	private Date warrantyEndDate;
	private String version;
	private Cluster cluster;
	private List<Shelve> shelves;
	private String asupBizkey;
	private List<Aggregate> aggregates;
	private double usedSpace;
	private double allocatedSpace;
	private double availSpace;
	private double usedPct;
	private String partnerNodeName;
	private String partnerSystemId;
	private String siteName;
	private Sod sodSchein;
	private List<Adapter> adaptes;

	public Node() {
	}

	public Node(String nodeName, String version, String serialNumber, String model, Date warrantyEndDate,
			List<Shelve> shelves, String asupBizkey) {
		super();
		this.nodeName = nodeName;
		this.version = version;
		this.serialNumber = serialNumber;
		this.model = model;
		this.warrantyEndDate = warrantyEndDate;
		this.shelves = shelves;
		this.asupBizkey = asupBizkey;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getSerialnumber() {
		return serialNumber;
	}

	public void setSerialnumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
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

	public Cluster getCluster() {
		return cluster;
	}

	public void setCluster(Cluster cluster) {
		this.cluster = cluster;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getAsupBizkey() {
		return asupBizkey;
	}

	public void setAsupBizkey(String asupBizkey) {
		this.asupBizkey = asupBizkey;
	}

	public List<Aggregate> getAggregates() {
		return aggregates;
	}

	public void setAggregates(List<Aggregate> aggregates) {
		this.aggregates = aggregates;
	}

	public double getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(double usedSpace) {
		this.usedSpace = usedSpace;
	}

	public double getAllocatedSpace() {
		return allocatedSpace;
	}

	public void setAllocatedSpace(double allocatedSpace) {
		this.allocatedSpace = allocatedSpace;
	}

	public double getAvailSpace() {
		return availSpace;
	}

	public void setAvailSpace(double availSpace) {
		this.availSpace = availSpace;
	}

	public double getUsedPct() {
		return usedPct;
	}

	public void setUsedPct(double usedPct) {
		this.usedPct = usedPct;
	}

	public String getPartnerNodeName() {
		return partnerNodeName;
	}

	public void setPartnerNodeName(String partnerNodeName) {
		this.partnerNodeName = partnerNodeName;
	}

	public String getPartnerSystemId() {
		return partnerSystemId;
	}

	public void setPartnerSystemId(String partnerSystemId) {
		this.partnerSystemId = partnerSystemId;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Sod getSodSchein() {
		return sodSchein;
	}

	public void setSodSchein(Sod sodSchein) {
		this.sodSchein = sodSchein;
	}

	public List<Adapter> getAdaptes() {
		return adaptes;
	}

	public void setAdaptes(List<Adapter> adaptes) {
		this.adaptes = adaptes;
	}
}
