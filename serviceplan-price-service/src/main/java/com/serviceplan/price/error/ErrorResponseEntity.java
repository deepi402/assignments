package com.serviceplan.price.error;

import org.springframework.http.HttpStatus;

/**
 * Used to wrap status code, message and other information for reporting back to
 * client
 * 
 * @author deepi
 *
 * @param <T>
 */
public class ErrorResponseEntity<T> {
	private String message;
	private HttpStatus status;
	private T inputEntity;

	public ErrorResponseEntity(String message, HttpStatus status, T inputEntity) {
		this.inputEntity = inputEntity;
		this.message = message;
		this.status = status;
	}

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

	public T getInputEntity() {
		return inputEntity;
	}

	public void setInputEntity(T inputEntity) {
		this.inputEntity = inputEntity;
	}

}
