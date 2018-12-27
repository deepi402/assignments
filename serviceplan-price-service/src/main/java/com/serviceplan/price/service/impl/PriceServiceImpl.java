package com.serviceplan.price.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplan.price.entity.PriceByPlanCountry;
import com.serviceplan.price.repository.PriceRepository;

@Service
public class PriceServiceImpl implements com.serviceplan.price.service.PriceService {
	Logger logger = LoggerFactory.getLogger(PriceServiceImpl.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");

	@Autowired
	PriceRepository priceRepository;

	@Override
	public PriceByPlanCountry getPriceById(long id) {
		return priceRepository.findByPriceId(id);
	}

	/**
	 * To get all prices (past and current) for given country and service plan
	 */
	@Override
	public List<PriceByPlanCountry> getPriceByPlanCountry(int countryId, int servicePlanId) {
		return priceRepository.findAllPricesByPlanCountry(countryId, servicePlanId);
	}

	@Override
	public List<PriceByPlanCountry> getPriceByPlanCountryActiveStatus(int countryId, int servicePlanId,
			boolean isActive) {
		return priceRepository.findAllPricesByPlanCountry(countryId, servicePlanId, isActive);
	}

	@Override
	public List<PriceByPlanCountry> getPriceByCountry(int countryId) {
		return priceRepository.findAllPricesByCountry(countryId);
	}

	@Override
	public List<PriceByPlanCountry> getPriceByCountryActiveStatus(int countryId, boolean isActive) {
		return priceRepository.findAllPricesByCountry(countryId, isActive);
	}

	@Override
	public PriceByPlanCountry getPriceByPlanCountryEffectiveDate(int countryId, int servicePlanId,
			String effectiveDate) {
		return priceRepository.findPriceByPlanCountryEffectiveDate(countryId, servicePlanId, effectiveDate);
	}

	@Override
	public PriceByPlanCountry getPriceByPlanCountryEffectiveDate(int countryId, int servicePlanId, Date effectiveDate) {
		return getPriceByPlanCountryEffectiveDate(countryId, servicePlanId, sdf.format(effectiveDate));
	}

	@Override
	public List<PriceByPlanCountry> getPriceByActiveStatus(boolean active) {
		return priceRepository.findAllPrices(active);
	}

	@Override
	public List<PriceByPlanCountry> getAllPrices() {
		Iterable<PriceByPlanCountry> it = priceRepository.findAll();
		List<PriceByPlanCountry> priceList = new ArrayList<>();
		it.forEach(price -> {
			priceList.add(price);
		});
		return priceList;
	}

	@Override
	public PriceByPlanCountry InsertPrice(PriceByPlanCountry price) {
		Date today = new Date();

		// if price's effectiveDate==today, mark previous active effective
		// price as active=false
		boolean isPriceEffectiveToday = sdf.format(price.getEffectiveFrom()).equals(sdf.format(today));
		if (isPriceEffectiveToday) {
			PriceByPlanCountry currentEffectivePrice = priceRepository.findCurrentEffectivePriceByPlanCountry(
					price.getCountryId(), price.getServicePlanId(), sdf.format(today));
			if (currentEffectivePrice != null) {
				currentEffectivePrice.setActive(false);
				currentEffectivePrice.setLastUpdated(new Date());
				priceRepository.save(currentEffectivePrice); // inactivate previous effective price
			}
		}
		price.setLastUpdated(new Date());
		return priceRepository.save(price); // insert new effective price
	}

	@Override
	public PriceByPlanCountry UpdatePrice(PriceByPlanCountry price) {
		return InsertPrice(price);
	}

	@Override
	public void deletePrice(long priceId) {
		priceRepository.deleteById(priceId);
	}

}
