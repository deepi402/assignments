package com.serviceplan.price.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * This class contains information about the service plan subscribed by a
 * customer. Customer can move from one service plan to another service plan
 * (example: 1S to 2S, 2S to 4S, 4S to 2S back). For each different subscription
 * plan, there will be a separate record with its own start and end date and
 * isActive flag indicating if the service plan is currently active.
 * 
 * @author deepi
 *
 */
@Entity
@Table
public class CustomerSubscription {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long subscriptionId;

	long customerId; // customerId in CustomerAccount table
	int servicePlanId; // service plan subscribed by customer
	double planPrice; // current price for subscribed plan
	Date updatePriceTime; // the time price was updated for the account
	Date planStart; // date on which customer started this plan
	Date planEnd; // date on which customer ended this plan
	boolean isActive; // if this is the active plan
	int countryId; // Country where customer lives (based on his current address)

	public long getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(long subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public int getServicePlanId() {
		return servicePlanId;
	}

	public void setServicePlanId(int service_plan_id) {
		this.servicePlanId = service_plan_id;
	}

	public double getPlanPrice() {
		return planPrice;
	}

	public void setPlanPrice(double planPrice) {
		this.planPrice = planPrice;
	}

	public Date getUpdatePriceTime() {
		return updatePriceTime;
	}

	public void setUpdatePriceTime(Date updatePriceTime) {
		this.updatePriceTime = updatePriceTime;
	}

	public Date getPlanStart() {
		return planStart;
	}

	public void setPlanStart(Date planStart) {
		this.planStart = planStart;
	}

	public Date getPlanEnd() {
		return planEnd;
	}

	public void setPlanEnd(Date planEnd) {
		this.planEnd = planEnd;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public int getCountryId() {
		return countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

}
