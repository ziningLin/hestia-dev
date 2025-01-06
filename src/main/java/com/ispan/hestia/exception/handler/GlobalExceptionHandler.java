package com.ispan.hestia.exception.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.ispan.hestia.exception.BaseException;
import com.ispan.hestia.exception.JWTException;

/**
 *  全局異常處理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
	 *  處理所有不可知異常 (可能會影響到所有交易管理)
	 */
//	@ExceptionHandler(Throwable.class)
//	public ResponseEntity<ApiError> handleException(Throwable e){
//		
//		return buildResponseEntity(ApiError.error(e.getMessage()));
//	}
	
	/**
	 * 處理所有 BaseCustomException
	 */
	@ExceptionHandler(BaseException.class)
	public ResponseEntity<ApiError> handleBaseCustomException(BaseException e) {
		return buildResponseEntity(ApiError.error(e.getStatus(), e.getMessage(), e.getErrorCode()));
	}
	
    /**
     * 處理所有接口數據驗證異常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){

        ObjectError objectError = e.getBindingResult().getAllErrors().get(0);
        String message = objectError.getDefaultMessage();
        if (objectError instanceof FieldError) {
            message = ((FieldError) objectError).getField() + ": " + message;
        }
		return buildResponseEntity(ApiError.error(HttpStatus.BAD_REQUEST.value(), message));
    }
    
	/**
	 * 處理缺少必要請求參數異常
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiError> handleMissingServletRequestParameterException(
			MissingServletRequestParameterException e) {
		String message = String.format("缺少必要的請求參數: %s", e.getParameterName());
		return buildResponseEntity(ApiError.error(HttpStatus.BAD_REQUEST.value(), message));
	}

	/**
	 * 處理參數類型不匹配異常
	 */
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		String parameterName = e.getName();
		String requiredType = e.getRequiredType() != null ? e.getRequiredType().getSimpleName() : "Unknown";
		String message = String.format("參數 '%s' 的值不正確，期望的類型是: %s", parameterName, requiredType);
		return buildResponseEntity(ApiError.error(HttpStatus.BAD_REQUEST.value(), message));
	}

	/**
	 * 處理 JWTException 使用401來代表(未授權)行為
	 */
	@ExceptionHandler(value = JWTException.class)
	public ResponseEntity<ApiError> handleJWTException(JWTException e) {
		return buildResponseEntity(ApiError.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
	}

	/**
	 * 統一格式
	 */
	private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError){
		return new ResponseEntity<>(apiError, HttpStatus.valueOf(apiError.getStatus()));
	}
}
