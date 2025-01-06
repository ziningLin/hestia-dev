package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;

/**
 * JWT相關異常
 */
public class JWTException extends BaseException {

	public JWTException(String msg) {
		super(msg, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
	}
}
