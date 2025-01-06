package com.ispan.hestia.exception;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * 「實體已經存在」
 */
public class EntityExistException extends BaseException {
	
	/**
	 * @param clazz 查找的實體類
	 * @param field	查找的屬性
	 * @param val	查找的屬性值
	 */
	public EntityExistException(Class<?> clazz, String field, String val) {
		super(EntityExistException.generateMessage(clazz.getSimpleName(), field, val), HttpStatus.CONFLICT,
				"ENTITY_EXIST");
    }

    private static String generateMessage(String entity, String field, String val) {
        return StringUtils.capitalize(entity)
                + " with " + field + " "+ val + " existed";
    }
}
