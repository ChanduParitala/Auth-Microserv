package com.mfpe.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mfpe.model.AuthenticationResponse;
/**
 * This class is responsible for handling All the Exceptions.
 *
 * */
@ControllerAdvice
public class ExceptionHandlerAdvice { // this class handles exception

	public static final Logger log = LoggerFactory.getLogger("Auth-Exception-Handler-Advice");

	// here it handles if any exception occurs during validation...
	 /**
     * This exception function is called when system encountered for Bad Credentials
     * @param BadCredentialException
     * @return exception message
     *  */	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> validationExceptions(MethodArgumentNotValidException formatException) {
		formatException.getBindingResult().getAllErrors().forEach((error) -> {
			log.error(error.getDefaultMessage());
		});
		return new ResponseEntity<String>("Give Username and Password in proper-format", HttpStatus.FORBIDDEN);
	}

	 /**
     * @param Exception
     * @return Exception message.*/
	@ExceptionHandler
	public ResponseEntity<Object> exception(Exception invalidUserException) {
		log.error(invalidUserException.getMessage());
		return new ResponseEntity<>(new AuthenticationResponse("Invalid", "Invalid", false), HttpStatus.OK);
	}
}
