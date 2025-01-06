package com.ispan.hestia.exception.handler;

import lombok.Data;


@Data
public class ApiError {
	
	private Integer status = 400;
	private Long timestamp;
	private String message;
	private String errorCode;
	
	private ApiError(){
		timestamp = System.currentTimeMillis();
	}
	
	public static ApiError error(String message) {
		ApiError apiError = new ApiError();
		apiError.setMessage(message);
		return apiError;
	}
	
	public static ApiError error(Integer status,String message) {
		ApiError apiError = new ApiError();
		apiError.setStatus(status);
		apiError.setMessage(message);
		return apiError;
	}

	public static ApiError error(Integer status, String message, String errorCode) {
		ApiError apiError = new ApiError();
		apiError.setStatus(status);
		apiError.setMessage(message);
		apiError.setErrorCode(errorCode);
		return apiError;
	}
}
