package com.serviceplan.price.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.serviceplan.price.entity.PriceByPlanCountry;

public interface PriceRepository extends CrudRepository<PriceByPlanCountry, Long> {

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.priceId = ?1")
	PriceByPlanCountry findByPriceId(long priceId);

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.countryId = ?1 and p.servicePlanId = ?2")
	List<PriceByPlanCountry> findAllPricesByPlanCountry(int countryId, int servicePlanId);

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.countryId = ?1 and p.servicePlanId = ?2 and p.isActive = ?3")
	List<PriceByPlanCountry> findAllPricesByPlanCountry(int countryId, int servicePlanId, boolean active);

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.countryId = ?1")
	List<PriceByPlanCountry> findAllPricesByCountry(int countryId);

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.countryId = ?1 and p.isActive = ?2")
	List<PriceByPlanCountry> findAllPricesByCountry(int countryId, boolean active);

	@Query(value = "SELECT p FROM PriceByPlanCountry p where p.isActive = ?1")
	List<PriceByPlanCountry> findAllPrices(boolean active);

	@Query(value = "UPDATE PriceByPlanCountry p set p.price= ?1, p.effectiveFrom = ?2, p.isActive = ?3 where p.countryId = ?4 and p.servicePlanId = ?5")
	int updatePrice(double price, Date effectiveFrom, boolean active, int countryId, int servicePlanId);

	@Query(value = "select * from PRICE_BY_PLAN_COUNTRY p where p.COUNTRY_ID = ?1 and p.SERVICE_PLAN_ID = ?2 and p.EFFECTIVE_FROM = ?3", nativeQuery = true)
	PriceByPlanCountry findPriceByPlanCountryEffectiveDate(int countryId, int servicePlanId, String effectiveFrom);

	@Query(value = "select * from PRICE_BY_PLAN_COUNTRY p where p.COUNTRY_ID = ?1 and p.SERVICE_PLAN_ID = ?2 and p.EFFECTIVE_FROM < ?3 and p.IS_ACTIVE=true", nativeQuery = true)
	PriceByPlanCountry findCurrentEffectivePriceByPlanCountry(int countryId, int servicePlanId, String effectiveFrom);

	@Query(value = "select * from PRICE_BY_PLAN_COUNTRY where COUNTRY_ID = ?1 and SERVICE_PLAN_ID = ?2 order by EFFECTIVE_FROM desc limit 1", nativeQuery = true)
	PriceByPlanCountry findLastEffectivePriceByPlanCountry(int countryId, int servicePlanId);
}
