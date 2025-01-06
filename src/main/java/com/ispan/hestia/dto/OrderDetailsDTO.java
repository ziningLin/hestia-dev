package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderDetailsDTO(

        // Integer orderId, Date checkInDate,
        // String roomName, Integer purchasedPrice, Date orderedDate, Integer singleBed,
        // Integer doubleBed, Integer bedroomCount, Double chechinTime, Double
        // chechoutTime, Integer activeRefundRequest) {
        // =======
        Integer orderId, Integer orderRoomId, Date checkInDate,
        String roomName, Integer purchasedPrice,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date orderingDate,
        Date bookededDate, Integer singleBed,
        Integer doubleBed, Integer bedroomCount, Double checkinTime, Double checkoutTime, String state,
        Integer stateId, Integer activeRefundRequest, byte[] mainImage, String roomAddr, String cityName,
        String roomNotice, String providerName, String providerContactInfo) {

}
