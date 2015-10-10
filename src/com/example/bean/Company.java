package com.example.bean;

import java.util.List;

public class Company {
	private String id;
	private String name;
	private List<Wind> windArray;
	
	public Company(String id, String name, List<Wind> windArray) {
		super();
		this.id = id;
		this.name = name;
		this.windArray = windArray;
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
	public List<Wind> getWindArray() {
		return windArray;
	}
	public void setWindArray(List<Wind> windArray) {
		this.windArray = windArray;
	}
	
	
}
