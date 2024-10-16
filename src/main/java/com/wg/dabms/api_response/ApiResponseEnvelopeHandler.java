package com.wg.dabms.api_response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wg.dabms.envelope.ResponseEnvelope;

public class ApiResponseEnvelopeHandler {
	public static ResponseEntity<Object> buildResponse(ApiResponseStatus status, HttpStatus statusCode, String message,
			Object data) {
		ResponseEnvelope apiResponse = new ResponseEnvelope(status, message, data);
		return new ResponseEntity<>(apiResponse, statusCode);
	}
}