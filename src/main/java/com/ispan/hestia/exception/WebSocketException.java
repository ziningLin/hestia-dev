package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;

/**
 * ws連線異常
 */
public class WebSocketException extends BaseException{

	public WebSocketException(String msg) {
		super(msg, HttpStatus.SERVICE_UNAVAILABLE, "WEBSOCKET_CONNECTION_FAILED");
	}
}
