package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderProviderDTO(
                Integer orderId,
                Integer orderRoomId,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date orderingDate,
                Date bookedDate,
                Date checkInDate,
                Integer purchasedPrice,
                String userName,
                String roomName,
                String state) {

}
