package com.serviceplan.price.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.serviceplan.price.entity.Country;
import com.serviceplan.price.repository.CountryRepository;
import com.serviceplan.price.service.CountryService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CountryServiceImplTest {
	@Autowired
	CountryService countryService;

	@Autowired
	CountryRepository countryRepository;

	@Before
	public void setup() {
		// clean table before each test
		countryRepository.deleteAll();
	}

	@Test
	public void shouldReturnNullWhenCountryDoesNotExists() {
		Country testCountry = countryService.getCountryByName("USA");
		Assertions.assertThat(testCountry).isNull();
	}

	@Test
	public void shouldReturnCountryWhenInserted() {
		Country testCountry = countryService.getCountryByName("TEST_COUNTRY");
		Assertions.assertThat(testCountry).isNull();

		testCountry = new Country();
		testCountry.setCountryName("TEST_COUNTRY"); // ID is generated automatically
		Country resultCountry = countryRepository.save(testCountry);

		Assertions.assertThat(resultCountry).isNotNull();
		Assertions.assertThat(resultCountry.getCountryName()).isEqualTo("TEST_COUNTRY");

	}

}
