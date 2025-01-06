package com.ispan.hestia.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DateUtil {

	private static final String DEFAULT_FORMAT = "yyyy-MM-dd";
	
	/**
     * 將 Date 轉換為指定格式的 String
     * @param date 日期對象
     * @param format 日期格式
     * @return 格式化後的日期字串
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    /**
     * 將 String 轉換為 Date
     * @param dateString 日期字串
     * @param format 日期格式
     * @return 轉換後的 Date 對象
     */
    public static Date parseDate(String dateString, String format) {
        if (dateString == null || format == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(dateString);
        } catch (Exception e) {
            throw new RuntimeException("日期解析失敗，請確認日期格式是否正確: " + dateString, e);
        }
    }
    
    /**
     * 使用預設格式（yyyy-MM-dd）將 String 轉換為 Date
     * @param dateString 日期字串
     * @return 轉換後的 Date 對象
     */
    public static Date parseDate(String dateString) {
        return parseDate(dateString, DEFAULT_FORMAT);
    }
    
    /**
     * 將 LocalDateTime 轉換為指定格式的 String
     * @param dateTime LocalDateTime 物件
     * @param format 日期格式
     * @return 格式化後的日期字串
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, String format) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    
    /* get next Date */
    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, 1); // 增加一天
        return calendar.getTime();
    }

    /* imput startDate and endDate and get the full DatesList */
    public static List<Date> getDatesList(Date startDate, Date endDate) {
        List<Date> dateList = new ArrayList<>();
        
        // 使用Calendar來操作日期
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(startDate);
        
        while (calendar.getTime().before(endDate)) {
            System.out.println("yes");
            dateList.add(calendar.getTime());
            calendar.add(java.util.Calendar.DATE, 1); // 日期加1
        }
        
        return dateList;
    }
	
}
