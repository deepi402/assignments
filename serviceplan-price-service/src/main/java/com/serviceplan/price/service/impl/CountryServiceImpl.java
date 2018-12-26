package com.serviceplan.price.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplan.price.entity.Country;
import com.serviceplan.price.repository.CountryRepository;

@Service
public class CountryServiceImpl implements com.serviceplan.price.service.CountryService {

	@Autowired
	CountryRepository countryRepository;

	@Override
	public Optional<Country> getCountryById(int id) {
		return countryRepository.findById(id);
	}

	@Override
	public Country getCountryByName(String countryName) {
		return countryRepository.getCountryByName(countryName);
	}

}
