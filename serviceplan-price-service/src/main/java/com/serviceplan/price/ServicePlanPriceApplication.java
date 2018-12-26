package com.serviceplan.price;

import java.io.IOException;
import java.text.ParseException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.serviceplan.price.dataloader.DataLoader;

@SpringBootApplication
public class ServicePlanPriceApplication {
	Logger logger = LoggerFactory.getLogger(ServicePlanPriceApplication.class);
	private static final String SAMPLE_DATA_DIR = "src/main/resources/sampledata/";

	@Autowired
	DataLoader dataLoader;

	public static void main(String[] args) {
		SpringApplication.run(ServicePlanPriceApplication.class, args);
	}

	@PostConstruct
	private void setupData() throws IOException, ParseException {
		dataLoader.loadCountryData(SAMPLE_DATA_DIR + "countries.csv");
		dataLoader.loadServicePlanData(SAMPLE_DATA_DIR + "service_plans.csv");
		dataLoader.loadCustomerAccountData(SAMPLE_DATA_DIR + "customer_accounts.csv");
		dataLoader.loadCustomerSubscriptionData();
		dataLoader.loadPriceData(SAMPLE_DATA_DIR + "price_by_country_and_plan.csv");
	}

}
