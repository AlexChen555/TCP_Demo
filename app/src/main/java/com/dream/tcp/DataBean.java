package com.dream.tcp;

public class DataBean {

	private String id;//
	private String name;// 
	private String portID;//
	private String value;//

	public DataBean(String id, String name, String portID, String value) {
		super();
		this.id = id;
		this.name = name;
		this.portID = portID;
		this.value = value;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPortID() {
		return portID;
	}
	public void setPortID(String portID) {
		this.portID = portID;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
