package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 自定義異常基類
 */
@Getter
public class BaseException extends RuntimeException {

	private final Integer status;
	private final String errorCode;

	public BaseException(String message, HttpStatus status, String errorCode) {
		super(message);
		this.status = status.value();
		this.errorCode = errorCode;
	}
}