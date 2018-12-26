package com.serviceplan.price.service;

import java.util.Date;
import java.util.List;

import com.serviceplan.price.entity.PriceByPlanCountry;

public interface PriceService {

	public PriceByPlanCountry getPriceById(long id);

	public List<PriceByPlanCountry> getPriceByPlanCountry(int countryId, int planId);

	public List<PriceByPlanCountry> getPriceByPlanCountryActiveStatus(int countryId, int planId, boolean isActive);

	public List<PriceByPlanCountry> getPriceByCountry(int countryId);

	public List<PriceByPlanCountry> getPriceByCountryActiveStatus(int countryId, boolean isActive);

	public PriceByPlanCountry getPriceByPlanCountryEffectiveDate(int countryId, int servicePlanId,
			String effectiveDate);

	public PriceByPlanCountry getPriceByPlanCountryEffectiveDate(int countryId, int servicePlanId, Date effectiveDate);

	public List<PriceByPlanCountry> getPriceByActiveStatus(boolean active);

	public List<PriceByPlanCountry> getAllPrices();

	public PriceByPlanCountry InsertPrice(PriceByPlanCountry price);

	public PriceByPlanCountry UpdatePrice(PriceByPlanCountry price);

	public void deletePrice(long priceId);

}
