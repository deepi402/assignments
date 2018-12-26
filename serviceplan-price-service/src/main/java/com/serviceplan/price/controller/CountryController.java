package com.serviceplan.price.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplan.price.entity.Country;
import com.serviceplan.price.service.impl.CountryServiceImpl;

@RestController
@RequestMapping(value = { "/countries" })
public class CountryController {

	@Autowired
	CountryServiceImpl countryService;

	/**
	 * Get country information for provided country id
	 * 
	 * @param id - country id
	 * @return
	 */
	@GetMapping(value = "/{id}")
	public ResponseEntity<Country> getCountryById(@PathVariable("id") int id) {
		Optional<Country> country = countryService.getCountryById(id);
		if (country.isPresent())
			return new ResponseEntity<Country>(country.get(), HttpStatus.OK);
		else
			return new ResponseEntity<Country>(HttpStatus.NOT_FOUND);
	}
}
