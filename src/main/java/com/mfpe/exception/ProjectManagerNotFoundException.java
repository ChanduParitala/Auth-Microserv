package com.mfpe.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectManagerNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	public static final Logger logger = LoggerFactory.getLogger("Project Manager Not Found-Exception-Handler-Advice");
	private static final long serialVersionUID = 1L;

	public ProjectManagerNotFoundException(String message) {
		logger.error(message);

	}

}
