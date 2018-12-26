package com.serviceplan.price.service;

import java.util.Optional;

import com.serviceplan.price.entity.Country;

public interface CountryService {

	public Optional<Country> getCountryById(int id);

	public Country getCountryByName(String countryName);

}
