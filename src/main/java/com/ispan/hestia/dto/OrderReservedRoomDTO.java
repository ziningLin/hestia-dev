package com.ispan.hestia.dto;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderReservedRoomDTO(
        Date checkInDate,
        Integer orderId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date orderingDate,
        String roomName,
        Integer roomId,
        Integer userId,
        String userName,
        String userContactInfo,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei") Date minDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei") Date maxDate,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Taipei") Date maxDatePlusOne,
        byte[] mainImage,
        Long roomCount,
        String roomAddr,
        String cityName,
        String roomNotice,
        Integer providerUserId,
        String providerName,
        String providerContactInfo,
        Double checkinTime, Double checkoutTime,
        Integer singleBed, Integer doubleBed,
        Integer bedroomCount, Integer bathroom) {

    // 供 JPQL 查詢使用的建構子，這個建構子自動計算 maxDatePlusOne
    public OrderReservedRoomDTO(Date checkInDate, Integer orderId, Date orderingDate, String roomName, Integer roomId,
            Integer userId,
            String userName,
            String userContactInfo, Date minDate, Date maxDate,
            String roomAddr, String cityName, String roomNotice, Integer providerUserId, String providerName,
            String providerContactInfo,
            Double checkinTime, Double checkoutTime, Integer singleBed, Integer doubleBed,
            Integer bedroomCount, Integer bathroom) {
        this(checkInDate, orderId, orderingDate, roomName, roomId, userId, userName, userContactInfo, minDate, maxDate,
                addOneDay(maxDate), null,
                null, roomAddr, cityName,
                roomNotice, providerUserId, providerName, providerContactInfo, checkinTime, checkoutTime, singleBed,
                doubleBed,
                bedroomCount, bathroom);
    }

    // 主建構子：接受所有欄位，並自動計算 maxDatePlusOne
    public OrderReservedRoomDTO(Date checkInDate, Integer orderId, Date orderingDate, String roomName, Integer roomId,
            Integer userId,
            String userName,
            String userContactInfo, Date minDate, Date maxDate,
            byte[] mainImage, Long roomCount, String roomAddr, String cityName, String roomNotice,
            Integer providerUserId,
            String providerName, String providerContactInfo, Double checkinTime, Double checkoutTime, Integer singleBed,
            Integer doubleBed,
            Integer bedroomCount, Integer bathroom) {
        this(checkInDate, orderId, orderingDate, roomName, roomId, userId, userName, userContactInfo, minDate, maxDate,
                addOneDay(maxDate),
                mainImage, roomCount, roomAddr,
                cityName, roomNotice, providerUserId, providerName, providerContactInfo, checkinTime, checkoutTime,
                singleBed,
                doubleBed, bedroomCount, bathroom);
    }

    // 靜態方法：用於增加一天
    private static Date addOneDay(Date date) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

}

// public record OrderReservedRoomDTO(Integer orderId, String roomName, Date
// minDate, Date maxDate,
// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
// Date maxDatePlusOne,
// byte[] mainImage, Long roomCount) {

// // 主建構函數：包含自動計算 maxDatePlusOne 的邏輯
// public OrderReservedRoomDTO(Integer orderId, String roomName, Date minDate,
// Date maxDate, byte[] mainImage,
// Long roomCount) {
// this(orderId, roomName, minDate, maxDate, addOneDay(maxDate), mainImage,
// roomCount);
// }

// // 靜態方法：用於增加一天
// private static Date addOneDay(Date date) {
// if (date == null) {
// return null;
// }
// Calendar calendar = Calendar.getInstance();
// calendar.setTime(date);
// calendar.add(Calendar.DATE, 1);
// return calendar.getTime();
// }

// @Override
// public String toString() {
// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
// return "RoomOrderDTO{" +
// "roomName='" + roomName + '\'' +
// ", minDate=" + (minDate != null ? sdf.format(minDate) : null) +
// ", maxDate=" + (maxDate != null ? sdf.format(maxDate) : null) +
// ", maxDatePlusOne=" + (maxDatePlusOne != null ? sdf.format(maxDatePlusOne) :
// null) +
// '}';
// }
// }
