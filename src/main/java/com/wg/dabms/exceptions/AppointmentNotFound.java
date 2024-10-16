package com.wg.dabms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AppointmentNotFound extends RuntimeException {
	public AppointmentNotFound(String message){
		super(message);
	}
}
