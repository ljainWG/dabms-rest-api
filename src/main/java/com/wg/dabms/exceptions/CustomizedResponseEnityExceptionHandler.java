package com.wg.dabms.exceptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ErrorDetailsEnvelope;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;
import com.wg.dabms.newexceptions.UnauthorizedAccessException;

//
@ControllerAdvice
//we are extends the class ResponseEntityExceptionHandler to create own own custom exception handling code by over-riding handleException() methods
public class CustomizedResponseEnityExceptionHandler extends ResponseEntityExceptionHandler {
	
	
//	@ExceptionHandler(UserNotFoundException.class) 	// Exception.class means all types of exceptions under exception class
//													//signature of handleException is used to create handleAllException() method
//	public final ResponseEntity<ErrorDetailsEnvelope> handleUserNotFoundException(Exception exception, WebRequest request) throws Exception {
//		System.out.println("customized exc");
//		ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), exception.getMessage(),request.getDescription(false));
//		return new ResponseEntity<ErrorDetailsEnvelope>(errorDetails, HttpStatus.NOT_FOUND);
//}
	@ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseEnvelopeWithPagination2> handleUserNotFoundException(UserNotFoundException ex) {
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message("Error")
                .data(null)
                .error(ex.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
	
//	@ExceptionHandler(UserNotFoundException.class)
//	public final ResponseEntity<ResponseEnvelopeWithPagination> handleUserNotFoundException(UserNotFoundException exception, WebRequest request) {
//		System.out.println("customized exc");
//		
//	    ResponseEnvelopeWithPagination errorResponse = ResponseEnvelopeWithPagination.builder()
//	            .status(ApiResponseStatus.ERROR )
//	            .message(exception.getMessage())
//	            .error("User does not exist.")
//	            .build();
//	    return new ResponseEntity<ResponseEnvelopeWithPagination>(errorResponse, HttpStatus.NOT_FOUND);
//	}

	
//	@ExceptionHandler(AppointmentNotFoundException.class)
//    public final ResponseEntity<ErrorDetailsEnvelope> handleAppointmentNotFoundException(Exception exception, WebRequest request) {
//        ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
//        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
//    }
	
	@ExceptionHandler(AppointmentNotFoundException.class)
	public final ResponseEntity<ResponseEnvelopeWithPagination> handleAppointmentNotFoundException(AppointmentNotFoundException exception, WebRequest request) {
	    ResponseEnvelopeWithPagination errorResponse = ResponseEnvelopeWithPagination.builder()
	            .status(ApiResponseStatus.ERROR)
	            .message(exception.getMessage())
	            .error("Appointment does not exist.")
	            .build();
	    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(UnauthorizedAccessException.class) // Handle UnauthorizedException
    public ResponseEntity<ErrorDetailsEnvelope> handleUnauthorizedException(UnauthorizedAccessException ex, WebRequest request) {
        ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN); // Return 403 Forbidden
    }

	

//	@ExceptionHandler annotation is used to tell what kind of exceptions we want to handle
	@ExceptionHandler(Exception.class) // Exception.class means all types of exceptions under exception class
//	signature of handleException is used to create handleAllException() method
	public final ResponseEntity<ErrorDetailsEnvelope> handleAllException(Exception exception, WebRequest request)
			throws Exception {
		ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), exception.getMessage(),
				request.getDescription(false));

		return new ResponseEntity<ErrorDetailsEnvelope>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	

	// handle all non valid argument error
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getFieldError().getDefaultMessage(), request.getDescription(false));
		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Total number of errors : "+exception.getErrorCount()+"\nFirst error is : "+exception.getFieldError().getDefaultMessage(), request.getDescription(false));
		
		// Collect all field errors with their messages in a simple, readable format
				
	    List<String> errorMessages = exception.getFieldErrors()
	                                          .stream()
	                                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
	                                          .collect(Collectors.toList());

	    // Create a default message that combines all error messages
	    String defaultErrorMessage = "Validation failed for fields: " + String.join(", ", errorMessages);

	    // Create the ErrorDetails object with the aggregated error message and request description
	    ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), defaultErrorMessage, request.getDescription(false));

	    // Return the response with the error details and a 400 BAD REQUEST status
		
		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);	
}

}