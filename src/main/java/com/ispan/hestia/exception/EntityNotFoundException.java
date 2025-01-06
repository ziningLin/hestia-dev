package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * 「找不到實體」
 */
public class EntityNotFoundException extends BaseException {
	
	/**
	 * @param clazz 查找的實體類
	 * @param field	查找的屬性
	 * @param val	查找的屬性值
	 */
	public EntityNotFoundException(Class<?> clazz, String field, String val) {
		super(EntityNotFoundException.generateMessage(clazz.getSimpleName(), field, val), HttpStatus.NOT_FOUND,
				"ENTITY_NOT_FOUND");
	}
	
	private static String generateMessage(String entity,String field,String val) {
		return StringUtils.capitalize(entity) + " with " + field + " "+val+" does not exsit";
	}
}
