package com.serviceplan.price.dataloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.serviceplan.price.entity.Country;
import com.serviceplan.price.entity.CustomerAccount;
import com.serviceplan.price.entity.CustomerSubscription;
import com.serviceplan.price.entity.PriceByPlanCountry;
import com.serviceplan.price.entity.ServicePlan;
import com.serviceplan.price.repository.CountryRepository;
import com.serviceplan.price.repository.CustomerAccountRepository;
import com.serviceplan.price.repository.CustomerSubscriptionRepository;
import com.serviceplan.price.repository.PriceRepository;
import com.serviceplan.price.repository.ServicePlanRepository;

/**
 * To load initial sample data
 * 
 * @author deepi
 *
 */
@Component
public class DataLoader {
	private static Logger logger = LoggerFactory.getLogger(DataLoader.class);
	private static Map<String, Integer> countryNameToIdMap = new HashMap<>();

	@Autowired
	ServicePlanRepository servicePlanRepository;

	@Autowired
	CustomerAccountRepository customerAccountRepository;

	@Autowired
	CustomerSubscriptionRepository customerSubscriptionRepository;

	@Autowired
	CountryRepository countryRepository;

	@Autowired
	PriceRepository priceRepository;

	public void loadCountryData(String filepath) throws IOException {
		InputStream is = new FileInputStream(filepath);
		List<Country> countries = CsvUtils.read(Country.class, is);
		countries.forEach(country -> {
			countryRepository.save(country);
		});
		logger.info(String.format("Read and loaded %d countries from file %s !!", countries.size(), filepath));
	}

	public void loadCustomerAccountData(String filepath) throws IOException {
		InputStream is = new FileInputStream(filepath);
		List<CustomerAccount> customerAccounts = CsvUtils.read(CustomerAccount.class, is);
		customerAccounts.forEach(ca -> {
			customerAccountRepository.save(ca);
		});
		logger.info(String.format("Read and loaded %d customer accounts from file %s !!", customerAccounts.size(),
				filepath));
	}

	public void loadServicePlanData(String filepath) throws IOException {
		InputStream is = new FileInputStream(filepath);
		List<ServicePlan> servicePlans = CsvUtils.read(ServicePlan.class, is);
		servicePlans.forEach(sp -> {
			servicePlanRepository.save(sp);
		});
		logger.info(String.format("Read and loaded %d service plans from file %s !!", servicePlans.size(), filepath));
	}

	public void loadCustomerSubscriptionData() throws IOException, ParseException {
		// load all countries to map country name to country id (Ideally this would be
		// read once and cached for reuse)
		Iterable<Country> countriesIterator = countryRepository.findAll();
		countriesIterator.forEach(country -> {
			countryNameToIdMap.put(country.getCountryName(), country.getCountryId());
		});

		SimpleDateFormat sdf = Constants.SIMPLE_DATE_FORMAT_YYYY_MM_DD;
		String dateInString = "2012-06-01";
		Date baseDate = sdf.parse(dateInString);

		Random random = new Random();
		Iterable<CustomerAccount> it = customerAccountRepository.findAll();
		final long count = 1;
		it.forEach(customerAccount -> {
			long mycount = count;
			CustomerSubscription customerSubscription = new CustomerSubscription();
			customerSubscription.setSubscriptionId(mycount++);
			customerSubscription.setCustomerId(customerAccount.getCustomerId());
			customerSubscription.setCountryId(countryNameToIdMap.get(customerAccount.getCountry()));
			customerSubscription.setActive(true);

			// randomly assign one of 3 plans (1,2,3)
			customerSubscription.setServicePlanId((random.nextInt() % 3) + 1);
			customerSubscription.setPlanStart(baseDate);
			customerSubscriptionRepository.save(customerSubscription);
		});
		logger.info(String.format("Loaded customer subscription data for all customers in customer_account table !!"));

	}

	public void loadPriceData(String filepath) throws IOException {
		InputStream is = new FileInputStream(filepath);
		List<PriceByPlanCountry> prices = CsvUtils.read(PriceByPlanCountry.class, is);
		prices.forEach(price -> {
			priceRepository.save(price);
		});
		logger.info(String.format("Read and loaded %d price data from file %s !!", prices.size(), filepath));
	}

}
