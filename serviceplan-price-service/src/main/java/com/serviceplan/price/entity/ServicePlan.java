package com.serviceplan.price.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class ServicePlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	int planId;

	String name;	//IS, 2S, 4S
    int numberOfConcurrentDevicesAllowed;
    String description;
    
    public int getPlanId() {
		return planId;
	}
	public void setPlanId(int planId) {
		this.planId = planId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumberOfConcurrentDevicesAllowed() {
		return numberOfConcurrentDevicesAllowed;
	}
	public void setNumberOfConcurrentDevicesAllowed(int numberOfConcurrentDevicesAllowed) {
		this.numberOfConcurrentDevicesAllowed = numberOfConcurrentDevicesAllowed;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
    
}
