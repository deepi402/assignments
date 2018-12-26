package com.serviceplan.price.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table
public class PriceByPlanCountry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "The database generated price ID")
	long priceId;

	@ApiModelProperty(notes = "ID of subscribed service plan")
	int servicePlanId; // planId in ServicePlan table

	@ApiModelProperty(notes = "ID of country")
	int countryId;

	@ApiModelProperty(notes = "Date when this price is effective from")
	Date effectiveFrom;

	@ApiModelProperty(notes = "Date when this record was last updated")
	Date lastUpdated;

	@ApiModelProperty(notes = "Price for this countryId, servicePlanId and effectiveFrom date")
	double price;

	@ApiModelProperty(notes = "To indicate if this price is currently in effect or not. It is set to false when a new effective price is rolled out")
	boolean isActive; // set to false when a new effective price is rolled out

	public long getPriceId() {
		return priceId;
	}

	public void setPriceId(long priceId) {
		this.priceId = priceId;
	}

	public int getServicePlanId() {
		return servicePlanId;
	}

	public void setServicePlanId(int servicePlanId) {
		this.servicePlanId = servicePlanId;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public Date getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(Date effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean getActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
