package com.wg.dabms.envelope;

import java.time.LocalDateTime;

// this class is created to define custom http-rest-body structure for exception response
// and custom exception class is created to handle the exception with custom exception-response body structure, that class name is CustomizedResponseEnityExceptionHandler
public class ErrorDetailsEnvelope {
//		error structure is mad up of datestamp, message and details
	private LocalDateTime timeStamp;
	private String message;
	private String details;

	public ErrorDetailsEnvelope(LocalDateTime timeStamp, String message, String details) {
		super();
		this.timeStamp = timeStamp;
		this.message = message;
		this.details = details;
	}

	public LocalDateTime getDateStamp() {
		return timeStamp;
	}

	public void setDateStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
