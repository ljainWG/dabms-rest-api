package com.wg.dabms.envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wg.dabms.api_response.ApiResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseEnvelope {
	
	@JsonProperty("status")
	private ApiResponseStatus status;
	@JsonProperty("message")
	private String message;
	@JsonProperty("data")
	private Object data;
	
}