package com.serviceplan.price.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import com.serviceplan.price.dataloader.Constants;
import com.serviceplan.price.entity.PriceByPlanCountry;
import com.serviceplan.price.error.ErrorResponseEntity;
import com.serviceplan.price.service.PriceService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Main class for receiving http requests from client for performing insert,
 * update, delete and retrieve operation on price
 * 
 * @author deepi
 *
 */
@RestController
@RequestMapping(value = { "/v1/price" })
@Api(value = "service plan price operations for 1=1S, 2=2S, 3=4S", description = "Operations pertaining to Netflix Service Plan Prices")
public class PriceController {
	private static final String OPERATION_NOT_ALLOWED_FOR_PAST_MSG = "Insert/Update/Delete operation not allowed for past (with effectiveFrom < today)";
	private static final String OPERATION_NOT_ALLOWED_FOR_FUTURE_MSG = "Insert not allowed for future price (with effectiveFrom > today)";
	private static final String NO_PRICE_INFO_FOUND_MSG = "No price information was found with Id = %s";
	private static final String PRICE_ALREADY_EXISTS_MSG = "Price already exists for same country, plan and effective date (ID = %s). Use PUT for price update.";
	private static final String ACTIVE_SHOULD_BE_TRUE_MSG = "Active field should be true";
	private static final String PRICE_ID_DOES_NOT_MATCH_COUNTRY_SERVICE_PLAN_AND_EFFECTIVE_FROM_MSG = "Specified priceId doesn't correspond to specified CountryId, servicePlanId, effectiveDate";
	private static final String INVALID_SERVICE_PLAN_ID_MSG = "servicePlanId is not in allowed range (1-3)";
	private static final String INVALID_DATE_FORMAT_MSG = "effectiveFrom should be \"yyyy-MM-dd\" format";
	private static final String INVALID_PRICE_MSG = "price can't be negative or zero";
	private static final String MISSING_EFFECTIVE_FROM_MSG = "effectiveFrom field should be no-null and in \\\"yyyy-MM-dd\\\" format";

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
	@ApiOperation(value = "Get all prices by countryId, planId and optional active status(true/false)")
	public ResponseEntity<List<PriceByPlanCountry>> getPriceByCountryAndPlan(@PathVariable("countryId") int countryId,
			@PathVariable("planId") int planId, @RequestParam(required = false, name = "active") String active) {
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
	@ApiOperation(value = "Get prices for all the plans by countryId and optional active status(true/false)")
	public ResponseEntity<List<PriceByPlanCountry>> getPriceByCountry(@PathVariable("countryId") int countryId,
			@RequestParam(required = false, name = "active") String active) {
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
	@ApiOperation(value = "Get prices for all the plans across all countries by optional active status(true/false)")
	public ResponseEntity<List<PriceByPlanCountry>> getPrice(
			@RequestParam(value = "active", required = false) String active) {
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
	@ApiOperation(value = "Bulk insert for price information using provided data (priceId field value will be ignored. No insert allowed for past date. Use PUT for updating existing price.")
	@ApiResponses({ @ApiResponse(code = 207, message = "Multi-status", response = ErrorResponseEntity.class), })
	public ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>> bulkInsert(
			@RequestBody List<PriceByPlanCountry> priceList, HttpServletRequest request, HttpServletResponse response) {

		// urls for successfully inserted prices
		StringBuffer links = new StringBuffer();
		int numInsertedPrice = 0;
		List<ErrorResponseEntity<PriceByPlanCountry>> errorResponseList = new ArrayList<>();

		for (PriceByPlanCountry price : priceList) {
			// validate effectiveDate, active and servicePlainId field
			ErrorResponseEntity<PriceByPlanCountry> errorResponseEntity = validateInput(price, true);
			if (errorResponseEntity != null) {
				errorResponseList.add(errorResponseEntity);
				continue;
			}

			// check if we already have price for given country, plan and effective date
			PriceByPlanCountry resultPrice = priceService.getPriceByPlanCountryEffectiveDate(price.getCountryId(),
					price.getServicePlanId(), price.getEffectiveFrom());

			// If already present report error
			if (resultPrice != null) {
				errorResponseEntity = new ErrorResponseEntity<>(
						String.format(PRICE_ALREADY_EXISTS_MSG, resultPrice.getPriceId()), HttpStatus.BAD_REQUEST,
						price);
				errorResponseList.add(errorResponseEntity);
				continue;
			}

			price.setPriceId(-1); // to automatically generate ID
			PriceByPlanCountry insertedPrice = priceService.InsertPrice(price);
			numInsertedPrice++;
			links.append(request.getRequestURL().append("/").append(insertedPrice.getPriceId()).toString()).append(",");

		}

		if (numInsertedPrice > 0) {
			links.deleteCharAt(links.length() - 1); // to remove last comma
			response.setHeader("Location", links.toString());
		}

		if (errorResponseList.size() > 0)
			return new ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>>(errorResponseList,
					HttpStatus.MULTI_STATUS);

		return new ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>>(HttpStatus.OK);

	}

	/**
	 * Bulk update price information using provided data
	 * 
	 * @param priceList - list of price to be updated
	 * @param request
	 * @param response
	 */
	@PutMapping(value = "")
	@ApiOperation(value = "Bulk update price information using provided data (Only price field is updated. No update allowed for past date.")
	public ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>> bulkUpdate(
			@RequestBody List<PriceByPlanCountry> priceList, HttpServletRequest request, HttpServletResponse response) {

		// urls for successfully updated prices
		StringBuffer links = new StringBuffer();
		int numUpdatedPrice = 0;
		List<ErrorResponseEntity<PriceByPlanCountry>> errorResponseList = new ArrayList<>();

		for (PriceByPlanCountry inputPrice : priceList) {

			// validate effectiveDate, active and servicePlainId field
			ErrorResponseEntity<PriceByPlanCountry> errorResponseEntity = validateInput(inputPrice, false);
			if (errorResponseEntity != null) {
				errorResponseList.add(errorResponseEntity);
				continue;
			}

			// check if inputPrice exists and not in past
			ErrorResponseEntity<Long> errorResponseEntity1 = validateInputPriceExistAndNotInPast(
					inputPrice.getPriceId());
			if (errorResponseEntity1 != null) {
				errorResponseEntity = new ErrorResponseEntity<>(errorResponseEntity1.getMessage(),
						errorResponseEntity1.getStatus(), inputPrice);
				errorResponseList.add(errorResponseEntity);
				continue;
			}

			PriceByPlanCountry resultPrice = priceService.getPriceById(inputPrice.getPriceId());

			// Now proceed with update in backend
			resultPrice.setPrice(inputPrice.getPrice());
			PriceByPlanCountry updatedPrice = priceService.UpdatePrice(resultPrice);
			numUpdatedPrice++;
			links.append(request.getRequestURL().append("/").append(updatedPrice.getPriceId()).toString()).append(",");
		}

		if (numUpdatedPrice > 0) {
			links.deleteCharAt(links.length() - 1); // to remove last comma
			response.setHeader("Location", links.toString());
		}

		if (errorResponseList.size() > 0)
			return new ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>>(errorResponseList,
					HttpStatus.MULTI_STATUS);

		return new ResponseEntity<List<ErrorResponseEntity<PriceByPlanCountry>>>(HttpStatus.OK);
	}

	/**
	 * Bulk Delete price information
	 * 
	 * @param priceList - list of price to be deleted
	 */
	@DeleteMapping(value = "")
	@ApiOperation(value = "Bulk Delete of price information. Only priceId field is used from input. No delete allowed for past date.")
	public ResponseEntity<List<ErrorResponseEntity<Long>>> bulkDelete(@RequestBody List<PriceByPlanCountry> priceList,
			HttpServletRequest request, HttpServletResponse response) {

		// urls for successfully deleted prices
		StringBuffer links = new StringBuffer();
		int numDeletedPrice = 0;
		List<ErrorResponseEntity<Long>> errorResponseList = new ArrayList<>();

		for (PriceByPlanCountry inputPrice : priceList) {

			ErrorResponseEntity<Long> errorResponseEntity = validateInputPriceExistAndNotInPast(
					inputPrice.getPriceId());
			if (errorResponseEntity != null) {
				errorResponseList.add(errorResponseEntity);
				continue;
			}

			// Now proceed with delete in backend
			priceService.deletePrice(inputPrice.getPriceId());
			numDeletedPrice++;
			links.append(request.getRequestURL().append("/").append(inputPrice.getPriceId()).toString()).append(",");
		}

		if (numDeletedPrice > 0) {
			links.deleteCharAt(links.length() - 1); // to remove last comma
			response.setHeader("Location", links.toString());
		}

		if (errorResponseList.size() > 0)
			return new ResponseEntity<List<ErrorResponseEntity<Long>>>(errorResponseList, HttpStatus.MULTI_STATUS);

		return new ResponseEntity<List<ErrorResponseEntity<Long>>>(HttpStatus.OK);

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
	@ApiOperation(value = "Update price information for given priceId using provided data (only price field is used and updated, all other fields are ignored. No update allowed for past date.)")
	public ResponseEntity<?> updatePriceByPriceId(@PathVariable("priceId") long priceId,
			@RequestBody PriceByPlanCountry newPriceInfo, HttpServletRequest request, HttpServletResponse response) {

		ErrorResponseEntity<Long> errorResponseEntity = validateInputPriceExistAndNotInPast(priceId);
		if (errorResponseEntity != null)
			return new ResponseEntity<String>(errorResponseEntity.getMessage(), errorResponseEntity.getStatus());

		PriceByPlanCountry priceInfo = priceService.getPriceById(priceId);

		// update not allowed with price <=0
		if (newPriceInfo.getPrice() <= 0) {
			return new ResponseEntity<String>(String.format(INVALID_PRICE_MSG), HttpStatus.BAD_REQUEST);
		}

		// update price
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
	@ApiOperation(value = "Delete price information for given priceId. No delete allowed for past date.")
	public ResponseEntity<String> deletePrice(@PathVariable("priceId") long priceId) {

		ErrorResponseEntity<Long> errorResponseEntity = validateInputPriceExistAndNotInPast(priceId);
		if (errorResponseEntity != null)
			return new ResponseEntity<String>(errorResponseEntity.getMessage(), errorResponseEntity.getStatus());

		priceService.deletePrice(priceId);
		return new ResponseEntity<String>("Price information was deleted successfully for " + priceId, HttpStatus.OK);
	}

	/**
	 * used to ensure that price information exists for given priceId and
	 * effectiveDate is not in past
	 * 
	 * @param priceId
	 * @return
	 * @throws ParseException
	 */
	private ErrorResponseEntity<Long> validateInputPriceExistAndNotInPast(long priceId) {
		// if price doesn't exist for given priceId
		PriceByPlanCountry priceInfo = priceService.getPriceById(priceId);
		if (priceInfo == null)
			return new ErrorResponseEntity<Long>(String.format(String.format(NO_PRICE_INFO_FOUND_MSG, priceId)),
					HttpStatus.NOT_FOUND, priceId);

		// if given priceId is for past date
		Date priceDate;
		try {
			// compare only yyyy-MM-dd
			SimpleDateFormat sdf = Constants.SIMPLE_DATE_FORMAT_YYYY_MM_DD;
			priceDate = sdf.parse(sdf.format(priceInfo.getEffectiveFrom()));
			Date today = sdf.parse(sdf.format(new Date()));
			if (priceDate.before(today))
				return new ErrorResponseEntity<Long>(String.format(String.format(OPERATION_NOT_ALLOWED_FOR_PAST_MSG)),
						HttpStatus.BAD_REQUEST, priceId);
		} catch (ParseException e) {
			return new ErrorResponseEntity<Long>(String.format(String.format(INVALID_DATE_FORMAT_MSG)),
					HttpStatus.BAD_REQUEST, priceId);
		}

		return null;
	}

	/**
	 * Method to validate input price information during insert
	 * 
	 * @param inputPrice
	 * @param errorResponse
	 * @return
	 * @throws ParseException
	 */
	private ErrorResponseEntity<PriceByPlanCountry> validateInput(PriceByPlanCountry inputPrice,
			boolean isInsertOperation) {

		// additional checks for insert operations
		if (isInsertOperation) {
			boolean isEffectiveFromSpecified = false;
			if (inputPrice.getEffectiveFrom() != null)
				isEffectiveFromSpecified = true;

			if (isEffectiveFromSpecified) {
				// Insert not allowed for past or future effectiveFrom date
				try {
					// compare only yyyy-MM-dd
					SimpleDateFormat sdf = Constants.SIMPLE_DATE_FORMAT_YYYY_MM_DD;
					Date priceDate = sdf.parse(sdf.format(inputPrice.getEffectiveFrom()));
					Date today = sdf.parse(sdf.format(new Date()));
					if (priceDate.before(today) || priceDate.after(today)) {
						String msg = (priceDate.before(today)) ? OPERATION_NOT_ALLOWED_FOR_PAST_MSG
								: OPERATION_NOT_ALLOWED_FOR_FUTURE_MSG;
						return new ErrorResponseEntity<PriceByPlanCountry>(String.format(msg), HttpStatus.BAD_REQUEST,
								inputPrice);
					}
				} catch (ParseException e) {
					return new ErrorResponseEntity<PriceByPlanCountry>(
							String.format(String.format(INVALID_DATE_FORMAT_MSG)), HttpStatus.BAD_REQUEST, inputPrice);
				}
			} else {
				return new ErrorResponseEntity<PriceByPlanCountry>(
						String.format(String.format(MISSING_EFFECTIVE_FROM_MSG)), HttpStatus.BAD_REQUEST, inputPrice);

			}

			// insert not allowed for servicePlanId outside 1-3
			int planId = inputPrice.getServicePlanId();
			if (planId < 1 || planId > 3) {
				return new ErrorResponseEntity<PriceByPlanCountry>(String.format(INVALID_SERVICE_PLAN_ID_MSG),
						HttpStatus.BAD_REQUEST, inputPrice);
			}

		}

		// insert/update not allowed with price <=0
		if (inputPrice.getPrice() <= 0) {
			return new ErrorResponseEntity<PriceByPlanCountry>(String.format(INVALID_PRICE_MSG), HttpStatus.BAD_REQUEST,
					inputPrice);
		}

		// insert/update not allowed with active=false status
		if (!inputPrice.getActive()) {
			return new ErrorResponseEntity<PriceByPlanCountry>(String.format(ACTIVE_SHOULD_BE_TRUE_MSG),
					HttpStatus.BAD_REQUEST, inputPrice);
		}

		return null;
	}

}
