package com.wg.dabms.newexceptions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.wg.dabms.api_response.ApiResponseStatus;
import com.wg.dabms.envelope.ResponseEnvelopeWithPagination2;

@ControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(UserNotFoundException2.class)
    public ResponseEntity<ResponseEnvelopeWithPagination2> handleUserNotFoundException(UserNotFoundException2 ex) {
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message(ex.getMessage())
                .data(null)
                .error("User Not Found")
                .timeStamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException2.class)
    public ResponseEntity<ResponseEnvelopeWithPagination2> handleUserAlreadyExistsException(UserAlreadyExistsException2 ex) {
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message(ex.getMessage())
                .data(null)
                .error("User Already Exists")
                .timeStamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // Handle other exceptions similarly
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseEnvelopeWithPagination2> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message(ex.getMessage())
                .data(null)
                .error("Unauthorized Access")
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN); // Return 403 Forbidden
    }
    
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ResponseEnvelopeWithPagination2> handleAllException(Exception exception, WebRequest request) {
        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message("An unexpected error occurred.")
                .data(null)
                .error(exception.getMessage())
                .timeStamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ResponseEnvelopeWithPagination2> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, WebRequest request) {
        List<String> errorMessages = exception.getFieldErrors()
            .stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.toList());

        String defaultErrorMessage = "Validation failed for fields: " + String.join(", ", errorMessages);

        ResponseEnvelopeWithPagination2 response = ResponseEnvelopeWithPagination2.builder()
                .status(ApiResponseStatus.ERROR)
                .message(defaultErrorMessage)
                .data(null)
                .error("Validation Error")
                .timeStamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // Return 400 BAD REQUEST
    }
    
////	@ExceptionHandler annotation is used to tell what kind of exceptions we want to handle
//	@ExceptionHandler(Exception.class) // Exception.class means all types of exceptions under exception class
////	signature of handleException is used to create handleAllException() method
//	public final ResponseEntity<ErrorDetailsEnvelope> handleAllException(Exception exception, WebRequest request)
//			throws Exception {
//		ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), exception.getMessage(),
//				request.getDescription(false));
//
//		return new ResponseEntity<ErrorDetailsEnvelope>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
//	}
//
//	
//
//	// handle all non valid argument error
//	@Override
//	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
//		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getMessage(), request.getDescription(false));
//		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), exception.getFieldError().getDefaultMessage(), request.getDescription(false));
//		//ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), "Total number of errors : "+exception.getErrorCount()+"\nFirst error is : "+exception.getFieldError().getDefaultMessage(), request.getDescription(false));
//		
//		// Collect all field errors with their messages in a simple, readable format
//				
//	    List<String> errorMessages = exception.getFieldErrors()
//	                                          .stream()
//	                                          .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
//	                                          .collect(Collectors.toList());
//
//	    // Create a default message that combines all error messages
//	    String defaultErrorMessage = "Validation failed for fields: " + String.join(", ", errorMessages);
//
//	    // Create the ErrorDetails object with the aggregated error message and request description
//	    ErrorDetailsEnvelope errorDetails = new ErrorDetailsEnvelope(LocalDateTime.now(), defaultErrorMessage, request.getDescription(false));
//
//	    // Return the response with the error details and a 400 BAD REQUEST status
//		
//		return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);	
//}
}
