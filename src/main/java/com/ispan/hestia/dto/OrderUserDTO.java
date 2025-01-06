package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderUserDTO(
        Integer orderId,
        // Integer totalPrice,
        // Long orderRoomCount,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date orderingDate,
        String state,
        Integer activeRefundRequest) {

}

// Date orderingDate,
// Date bookedDate,
// Date checkInDate,
// Integer purchasedPrice,
// String providerName,
// String roomName,
// String state) {

// }
