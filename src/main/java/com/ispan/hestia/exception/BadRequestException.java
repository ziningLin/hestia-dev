package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;
/**
 * 「請求無法被正確處理」
 * 參數錯誤、非預期輸入值...
 */
public class BadRequestException extends BaseException {

	public BadRequestException(String msg) {
		super(msg, HttpStatus.BAD_REQUEST, "BAD_REQUEST");
	}
	
	public BadRequestException(HttpStatus status, String msg) {
		super(msg, status, "BAD_REQUEST");
    }
}
