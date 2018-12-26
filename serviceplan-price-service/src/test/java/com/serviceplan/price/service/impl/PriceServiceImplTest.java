package com.serviceplan.price.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.serviceplan.price.entity.PriceByPlanCountry;
import com.serviceplan.price.repository.PriceRepository;
import com.serviceplan.price.service.PriceService;

@RunWith(SpringRunner.class)
@SpringBootTest

public class PriceServiceImplTest {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd");

	@Autowired
	PriceService priceService;

	@Autowired
	PriceRepository priceRepository;

	@Before
	public void setUp() throws Exception {
		// delete all data from table before each test
		priceRepository.deleteAll();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPriceById() throws ParseException {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);
		long priceId = inputPrice.getPriceId();

		// Now test getPriceById() to retrieve by priceId
		PriceByPlanCountry resultPrice = priceService.getPriceById(priceId);
		Assertions.assertThat(resultPrice).isNotNull();

		assertEquals(resultPrice, inputPrice);
	}

	@Test
	public void testGetPriceByPlanCountry() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Now test getPriceByPlanCountry() to retrieve
		List<PriceByPlanCountry> resultPrices = priceService.getPriceByPlanCountry(inputPrice.getCountryId(),
				inputPrice.getServicePlanId());
		Assertions.assertThat(resultPrices).isNotNull();
		Assertions.assertThat(resultPrices.size()).isEqualTo(1);
		assertEquals(resultPrices.get(0), inputPrice);
	}

	@Test
	public void testGetPriceByPlanCountryActiveStatus() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Now test getPriceByPlanCountryActiveStatus() to retrieve
		List<PriceByPlanCountry> resultPrices = priceService.getPriceByPlanCountryActiveStatus(
				inputPrice.getCountryId(), inputPrice.getServicePlanId(), inputPrice.getActive());
		Assertions.assertThat(resultPrices).isNotNull();
		Assertions.assertThat(resultPrices.size()).isEqualTo(1);
		assertEquals(resultPrices.get(0), inputPrice);
	}

	@Test
	public void testGetPriceByCountry() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Now test getPriceByCountry() to retrieve
		List<PriceByPlanCountry> resultPrices = priceService.getPriceByCountry(inputPrice.getCountryId());
		Assertions.assertThat(resultPrices).isNotNull();
		Assertions.assertThat(resultPrices.size()).isEqualTo(1);
		assertEquals(resultPrices.get(0), inputPrice);
	}

	@Test
	public void testGetPriceByCountryActiveStatus() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Now test getPriceByCountryActiveStatus() to retrieve
		List<PriceByPlanCountry> resultPrices = priceService.getPriceByCountryActiveStatus(inputPrice.getCountryId(),
				inputPrice.getActive());
		Assertions.assertThat(resultPrices).isNotNull();
		Assertions.assertThat(resultPrices.size()).isEqualTo(1);
		assertEquals(resultPrices.get(0), inputPrice);
	}

	@Test
	public void priceIsUpdatedWhenCountryPlanAndEffectiveDateRemainsSame() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);
		// Check to ensure that now there is 1 row in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(1);

		// Now test updatePrice() to make price changes
		inputPrice.setPrice(inputPrice.getPrice() + 100); // increase price by 100
		long priceId = priceService.UpdatePrice(inputPrice).getPriceId();

		// Again check to ensure that still there is only 1 row in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(1);

		PriceByPlanCountry resultPrice = priceRepository.findByPriceId(priceId);
		Assertions.assertThat(resultPrice).isNotNull();
		assertEquals(resultPrice, inputPrice);
	}

	@Test
	public void previousEffectivePriceIsInactivatedWhenNewPriceIsRolledOutWithTodayAsEffectiveDate()
			throws ParseException {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert price with effectiveFrom in past
		double oldPrice = 12.99;
		inputPrice.setPriceId(-1); // to autogenerate ID
		inputPrice.setEffectiveFrom(sdf.parse("2018-3-15"));
		inputPrice.setPrice(oldPrice);
		inputPrice.setActive(true);
		inputPrice = priceRepository.save(inputPrice);
		long oldPriceId = inputPrice.getPriceId();

		// Check to ensure that now there is 1 row in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(1);
		// ensure that effectiveDate, active and price fields are still same
		Assertions.assertThat(sdf.format(inputPrice.getEffectiveFrom())).isEqualTo("2018-3-15");
		Assertions.assertThat(inputPrice.getActive()).isTrue();
		Assertions.assertThat(inputPrice.getPrice()).isEqualTo(oldPrice);

		// Now insert new price with effectiveFrom as today's date
		double newPrice = 13.99;
		inputPrice.setPriceId(-1); // to autogenerate ID
		inputPrice.setEffectiveFrom(new Date());
		inputPrice.setPrice(newPrice);
		inputPrice.setActive(true);
		priceService.UpdatePrice(inputPrice);

		// Now check to ensure that there are 2 rows in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(2);

		// check the active for old price id changed to false
		PriceByPlanCountry oldPriceInfo = priceRepository.findByPriceId(oldPriceId);
		Assertions.assertThat(oldPriceInfo).isNotNull();
		Assertions.assertThat(sdf.format(oldPriceInfo.getEffectiveFrom())).isEqualTo("2018-3-15");
		Assertions.assertThat(oldPriceInfo.getActive()).isFalse();
		Assertions.assertThat(oldPriceInfo.getPrice()).isEqualTo(oldPrice);
	}

	@Test
	public void testGetPriceByPlanCountryEffectiveDate() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Now test getPriceByPlanCountryEffectiveDate() to retrieve
		PriceByPlanCountry resultPrice = priceService.getPriceByPlanCountryEffectiveDate(inputPrice.getCountryId(),
				inputPrice.getServicePlanId(), sdf.format(inputPrice.getEffectiveFrom()));
		Assertions.assertThat(resultPrice).isNotNull();
		assertEquals(resultPrice, inputPrice);
	}

	@Test
	public void testDeletePrice() {
		PriceByPlanCountry inputPrice = createPriceByPlanCountryInput();

		// Check to ensure that there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);

		// insert data
		inputPrice = priceRepository.save(inputPrice);

		// Check to ensure that now there is 1 row in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(1);

		// Now test deletePrice()
		priceService.deletePrice(inputPrice.getPriceId());

		// Check to ensure that data is deleted and there is no data in price table
		Assertions.assertThat(priceRepository.count()).isEqualTo(0);
	}

	private PriceByPlanCountry createPriceByPlanCountryInput() {
		PriceByPlanCountry inputPrice = new PriceByPlanCountry();
		inputPrice.setCountryId(1);
		inputPrice.setServicePlanId(1);
		inputPrice.setActive(true);
		Date today = new Date();
		inputPrice.setEffectiveFrom(today);
		inputPrice.setLastUpdated(today);
		inputPrice.setPrice(12.99);
		return inputPrice;
	}

	private void assertEquals(PriceByPlanCountry resultPrice, PriceByPlanCountry inputPrice) {
		Assertions.assertThat(resultPrice.getPriceId()).isEqualTo(inputPrice.getPriceId());
		Assertions.assertThat(resultPrice.getCountryId()).isEqualTo(inputPrice.getCountryId());
		Assertions.assertThat(resultPrice.getServicePlanId()).isEqualTo(inputPrice.getServicePlanId());
		Assertions.assertThat(resultPrice.getActive()).isEqualTo(inputPrice.getActive());
		// To compare only "yyyy-M-dd"
		Assertions.assertThat(sdf.format(resultPrice.getEffectiveFrom()))
				.isEqualTo(sdf.format(inputPrice.getEffectiveFrom()));
		// lastUpdated if populated during insertion as well
		Assertions.assertThat(sdf.format(resultPrice.getLastUpdated()))
				.isEqualTo(sdf.format(inputPrice.getLastUpdated()));
		Assertions.assertThat(resultPrice.getPrice()).isEqualTo(inputPrice.getPrice());

	}
}
