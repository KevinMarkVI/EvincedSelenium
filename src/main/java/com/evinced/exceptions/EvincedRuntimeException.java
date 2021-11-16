package com.evinced.exceptions;

/**
 * EvincedRuntimeException represents an error returned from running evinced SDK
 */

public class EvincedRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -123456789087654L;

	public EvincedRuntimeException(String message) {
		super(message);
	}
}

