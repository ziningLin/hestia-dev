package com.ispan.hestia.ws.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 提供靜態的方法來訪問 Spring 的上下文
 */
@Component
public class SpringContextHolder implements ApplicationContextAware{
	
	private static ApplicationContext context;

    /**
     * 設置 ApplicationContext 的靜態實例，以在應用中的任何地方訪問 Spring 上下文。
     * 
     * @param applicationContext - 傳入的 Spring ApplicationContext
     * @throws BeansException 
     */
    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        context = ctx;
    }

    /**
     * 獲取當前的 ApplicationContext。
     * 
     * @return ApplicationContext - 當前的 Spring 上下文
     */
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 通過類型獲取 Spring 管理的 Bean。
     * 
     * @param beanClass - 要獲取的 Bean 的類型
     * @param <T> 
     * @return T 
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }
}
