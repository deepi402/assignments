package com.serviceplan.price.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplan.price.entity.PriceByPlanCountry;
import com.serviceplan.price.service.PriceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(value = { "/v1/price" })
@Api(value = "service plan price operations for 1=1S, 2=2S, 3=4S", description = "Operations pertaining to Netflix Service Plan Prices")
public class PriceController {
	private static final String DELETE_OPERATION_NOT_ALLOWD_MSG = "Delete operation not allowed on past price with ID = %s";
	private static final String UPDATE_OPERATION_NOT_ALLOWD_MSG = "Update operation not allowed on past price with ID = %s";
	private static final String NO_PRICE_INFO_FOUND_MSG = "No price information was found with Id = %s";
	private static final String PRICE_ALREADY_EXISTS_MSG = "Price already exists for same country, plan and effective date (ID = %s). Use PUT for price update.";
	private static final String ACTIVE_SHOULD_BE_TRUE_MSG = "Active field should be false";
	private static final String EFFECTIVE_FROM_IS_IN_PAST_MSG = "effectiveFrom field can't be in past";
	private static final String PRICE_ID_DOES_MATCH_COUNTRY_SERVICE_PLAN_AND_EFFECTIVE_FROM_MSG = "Specified priceId doesn't correspond to specified CountryId, servicePlanId, effectiveDate";

	Logger logger = LoggerFactory.getLogger(PriceController.class);

	@Autowired
	PriceService priceService;

	/**
	 * Get price information for provided priceId
	 * 
	 * @param priceId
	 * @return
	 */
	@GetMapping(value = "/{priceId}")
	@ApiOperation(value = "Get price information for provided priceId", response = PriceByPlanCountry.class)
	public ResponseEntity<PriceByPlanCountry> getPriceById(@PathVariable("priceId") long priceId) {
		PriceByPlanCountry price = priceService.getPriceById(priceId);
		if (price != null)
			return new ResponseEntity<PriceByPlanCountry>(price, HttpStatus.OK);
		else
			return new ResponseEntity<PriceByPlanCountry>(HttpStatus.NOT_FOUND);
	}

	/**
	 * Get all prices by countryId, planId and active status(optional)
	 * 
	 * @param countryId
	 * @param planId
	 * @param isActive
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/countries/{countryId}/plans/{planId}")
	@ApiOperation(value = "Get all prices by countryId, planId and active(optional)")
	public ResponseEntity<List<PriceByPlanCountry>> getPriceByCountryAndPlan(@PathVariable("countryId") int countryId,
			@PathVariable("planId") int planId, @RequestParam(value = "true/false", required = false) String active) {
		List<PriceByPlanCountry> priceList = null;

		// if query parameter 'active' is specified, filter by active field
		if (active != null)
			priceList = priceService.getPriceByPlanCountryActiveStatus(countryId, planId, Boolean.valueOf(active));
		else
			priceList = priceService.getPriceByPlanCountry(countryId, planId);
		if (priceList != null && priceList.size() > 0)
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.OK);
		else
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.NOT_FOUND);
	}

	/**
	 * Get prices for all the plans by countryId and active status(optional)
	 * 
	 * @param countryId
	 * @param isActive
	 * @return
	 */
	@GetMapping(value = "/countries/{countryId}")
	@ApiOperation(value = "Get prices for all the plans by countryId and active status(optional)")
	public ResponseEntity<List<PriceByPlanCountry>> getPriceByCountry(@PathVariable("countryId") int countryId,
			@RequestParam(value = "true/false", required = false) String active) {
		List<PriceByPlanCountry> priceList = null;

		// if query parameter 'active' is specified
		if (active != null)
			priceList = priceService.getPriceByCountryActiveStatus(countryId, Boolean.valueOf(active));
		else
			priceList = priceService.getPriceByCountry(countryId);
		if (priceList != null && priceList.size() > 0)
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.OK);
		else
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.NOT_FOUND);
	}

	/**
	 * Get prices for all the plans across all countries by active status(optional)
	 * 
	 * @param countryId
	 * @param isActive
	 * @return
	 */
	@GetMapping(value = "")
	@ApiOperation(value = "Get prices for all the plans across all countries by active status(optional)")
	public ResponseEntity<List<PriceByPlanCountry>> getPrice(
			@RequestParam(value = "true/false", required = false) String active) {
		List<PriceByPlanCountry> priceList = null;

		// if query parameter 'active' is specified
		if (active != null)
			priceList = priceService.getPriceByActiveStatus(Boolean.valueOf(active));
		else
			priceList = priceService.getAllPrices();
		if (priceList != null && priceList.size() > 0)
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.OK);
		else
			return new ResponseEntity<List<PriceByPlanCountry>>(priceList, HttpStatus.NOT_FOUND);
	}

	/**
	 * Bulk insert price information using provided data
	 * 
	 * @param priceList                  - list of price to be created/updated
	 * @param disableOlderEffectivePrice - if set to true, it will also mark make
	 *                                   currently effective price inactive.
	 * @param request
	 * @param response
	 */
	@PostMapping(value = "")
	@ApiOperation(value = "Bulk insert for price information using provided data (priceId field will be ignored. No insert allowed for past date. active should be 'true' for today and future date. Use PUT for updating existing price.")
	@ApiResponses({ @ApiResponse(code = 207, message = "Multi-status", response = PriceStatus.class), })
	public ResponseEntity<List<PriceStatus>> bulkInsert(@RequestBody List<PriceByPlanCountry> priceList,
			HttpServletRequest request, HttpServletResponse response) {

		// urls to access inserted prices
		StringBuffer links = new StringBuffer();
		int numInsertedPrice = 0;
		List<PriceStatus> errorMessageList = new ArrayList<>();

		for (PriceByPlanCountry price : priceList) {
			// Insert not allowed for past date or with active=false status
			if (price.getEffectiveFrom().before(new Date())) {
				PriceStatus priceStatus = getPriceStatus(String.format(EFFECTIVE_FROM_IS_IN_PAST_MSG), price);
				errorMessageList.add(priceStatus);
				continue;
			}

			// insert not allowed for active=false status
			if (!price.getActive()) {
				PriceStatus priceStatus = getPriceStatus(String.format(ACTIVE_SHOULD_BE_TRUE_MSG), price);
				errorMessageList.add(priceStatus);
				continue;
			}

			// insert not allowed for servicePlanId outside 1-3
			int planId = price.getServicePlanId();
			if (planId < 1 || planId > 3) {
				PriceStatus priceStatus = getPriceStatus(String.format(ACTIVE_SHOULD_BE_TRUE_MSG), price);
				errorMessageList.add(priceStatus);
				continue;
			}

			// check if we already have price for given country, plan and effective date
			PriceByPlanCountry resultPrice = priceService.getPriceByPlanCountryEffectiveDate(price.getCountryId(),
					price.getServicePlanId(), price.getEffectiveFrom());

			// Only insert if not present already
			if (resultPrice == null) {
				price.setPriceId(-1); // to automatically generate ID
				PriceByPlanCountry insertedPrice = priceService.InsertPrice(price);
				numInsertedPrice++;
				links.append(request.getRequestURL().append("/").append(insertedPrice.getPriceId()).toString())
						.append(",");
			} else {
				PriceStatus priceStatus = getPriceStatus(
						String.format(PRICE_ALREADY_EXISTS_MSG, resultPrice.getPriceId()), price);
				errorMessageList.add(priceStatus);
			}
		}

		if (numInsertedPrice > 0) {
			links.deleteCharAt(links.length() - 1); // to remove last comma
			response.setHeader("Location", links.toString());
		}

		if (errorMessageList.size() > 0)
			return new ResponseEntity<List<PriceStatus>>(errorMessageList, HttpStatus.MULTI_STATUS);

		return new ResponseEntity<List<PriceStatus>>(HttpStatus.OK);

	}

	/**
	 * Bulk update price information using provided data
	 * 
	 * @param priceList - list of price to be created/updated
	 * @param request
	 * @param response
	 */
	@PutMapping(value = "")
	@ApiOperation(value = "Bulk update price information using provided data (Only price field is updated. All fields are mandatory")
	public ResponseEntity<List<PriceStatus>> bulkUpdate(@RequestBody List<PriceByPlanCountry> priceList,
			HttpServletRequest request, HttpServletResponse response) {
		// urls to access inserted prices
		StringBuffer links = new StringBuffer();
		int numUpdatedPrice = 0;
		List<PriceStatus> errorMessageList = new ArrayList<>();

		for (PriceByPlanCountry price : priceList) {
			// Update not allowed for past date or with active=false status
			if (price.getEffectiveFrom().before(new Date())) {
				PriceStatus priceStatus = getPriceStatus(String.format(EFFECTIVE_FROM_IS_IN_PAST_MSG), price);
				errorMessageList.add(priceStatus);
				continue;
			}

			// Update not allowed for active=false status
			if (!price.getActive()) {
				PriceStatus priceStatus = getPriceStatus(String.format(ACTIVE_SHOULD_BE_TRUE_MSG), price);
				errorMessageList.add(priceStatus);
				continue;
			}

			// check if we already have price for given country, plan and effective date
			PriceByPlanCountry resultPrice = priceService.getPriceByPlanCountryEffectiveDate(price.getCountryId(),
					price.getServicePlanId(), price.getEffectiveFrom());

			// Only Update if present already
			if (resultPrice != null) {
				// ensure priceId of retrieved price is same as the one specified in input
				if (resultPrice.getPriceId() != price.getPriceId()) {
					PriceStatus priceStatus = getPriceStatus(
							String.format(PRICE_ID_DOES_MATCH_COUNTRY_SERVICE_PLAN_AND_EFFECTIVE_FROM_MSG,
									resultPrice.getPriceId()),
							price);
					errorMessageList.add(priceStatus);
					continue;
				}

				PriceByPlanCountry insertedPrice = priceService.UpdatePrice(price);
				numUpdatedPrice++;
				links.append(request.getRequestURL().append("/").append(insertedPrice.getPriceId()).toString())
						.append(",");
			} else {
				PriceStatus priceStatus = getPriceStatus(String.format(NO_PRICE_INFO_FOUND_MSG, price.getPriceId()),
						price);
				errorMessageList.add(priceStatus);
			}
		}

		if (numUpdatedPrice > 0) {
			links.deleteCharAt(links.length() - 1); // to remove last comma
			response.setHeader("Location", links.toString());
		}

		if (errorMessageList.size() > 0)
			return new ResponseEntity<List<PriceStatus>>(errorMessageList, HttpStatus.MULTI_STATUS);

		return new ResponseEntity<List<PriceStatus>>(HttpStatus.OK);
	}

	/**
	 * update price information for given priceId using provided data (only price
	 * field is updated)
	 * 
	 * @param priceId      - priceId of price to be updated
	 * @param newPriceInfo - updated price info (only price field is updated)
	 * @param request
	 * @param response
	 * @return
	 */
	@PutMapping(value = "/{priceId}")
	@ApiOperation(value = "Update price information for given priceId using provided data (only price field is updated)")
	public ResponseEntity<String> updatePriceByPriceId(@PathVariable("priceId") long priceId,
			@RequestBody PriceByPlanCountry newPriceInfo, HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity<String> responseEntity = validateInput(priceId, UPDATE_OPERATION_NOT_ALLOWD_MSG);
		if (responseEntity.getStatusCode() != HttpStatus.OK)
			return responseEntity;

		// update price
		PriceByPlanCountry priceInfo = priceService.getPriceById(priceId);
		priceInfo.setPrice(newPriceInfo.getPrice());
		priceInfo = priceService.UpdatePrice(priceInfo);

		// provide link to updated price
		StringBuffer link = new StringBuffer();
		link.append(request.getRequestURL().append("/").append(priceInfo.getPriceId()).toString());
		response.setHeader("Location", link.toString());

		return new ResponseEntity<String>("Price information was updated successfully for " + priceId, HttpStatus.OK);

	}

	/**
	 * Delete price information for given priceId
	 * 
	 * @param priceId
	 */
	@DeleteMapping(value = "/{priceId}")
	@ApiOperation(value = "Delete price information for given priceId")
	public ResponseEntity<String> deletePrice(@PathVariable("priceId") long priceId) {

		ResponseEntity<String> responseEntity = validateInput(priceId, DELETE_OPERATION_NOT_ALLOWD_MSG);
		if (responseEntity.getStatusCode() != HttpStatus.OK)
			return responseEntity;

		priceService.deletePrice(priceId);
		return new ResponseEntity<String>("Price information was deleted successfully for " + priceId, HttpStatus.OK);
	}

	private ResponseEntity<String> validateInput(long priceId, String msg) {
		// if price doesn't exist for given priceId
		PriceByPlanCountry priceInfo = priceService.getPriceById(priceId);
		if (priceInfo == null)
			return new ResponseEntity<String>(String.format(NO_PRICE_INFO_FOUND_MSG, priceId), HttpStatus.NOT_FOUND);

		// if incoming price is for past date
		if (priceInfo.getEffectiveFrom().before(new Date()))
			return new ResponseEntity<String>(String.format(msg, priceId), HttpStatus.BAD_REQUEST);

		return new ResponseEntity<String>(HttpStatus.OK);
	}

	private PriceStatus getPriceStatus(String msg, PriceByPlanCountry input) {
		PriceStatus priceStatus = new PriceStatus();
		priceStatus.setInput(input);
		priceStatus.setStatus(HttpStatus.BAD_REQUEST);
		priceStatus.setMessage(msg);
		return priceStatus;
	}

	class PriceStatus {
		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public HttpStatus getStatus() {
			return status;
		}

		public void setStatus(HttpStatus status) {
			this.status = status;
		}

		public PriceByPlanCountry getInput() {
			return input;
		}

		public void setInput(PriceByPlanCountry input) {
			this.input = input;
		}

		String message;
		HttpStatus status;
		PriceByPlanCountry input;

	}

}
