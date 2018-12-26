package com.serviceplan.price.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.serviceplan.price.entity.Country;

public interface CountryRepository extends CrudRepository<Country, Integer> {
	@Query("SELECT c FROM Country c WHERE c.countryName = ?1")
	public Country getCountryByName(String countryName);
}
