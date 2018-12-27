package com.serviceplan.price.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import io.swagger.annotations.ApiModelProperty;

@Entity
@Table
public class PriceByPlanCountry {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ApiModelProperty(notes = "The database generated price ID")
	private long priceId;

	@ApiModelProperty(notes = "ID of subscribed service plan")
	private int servicePlanId;

	@ApiModelProperty(notes = "ID of country")
	private int countryId;

	@ApiModelProperty(notes = "Date when this price is effective from")
	@NotNull
	private Date effectiveFrom;

	@ApiModelProperty(notes = "Date when this record was last updated")
	private Date lastUpdated;

	@ApiModelProperty(notes = "Price for this countryId, servicePlanId and effectiveFrom date")
	@Positive
	private double price;

	@ApiModelProperty(notes = "To indicate if this price is currently in effect or not. It is set to false when a new effective price is rolled out")
	private boolean isActive;

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
