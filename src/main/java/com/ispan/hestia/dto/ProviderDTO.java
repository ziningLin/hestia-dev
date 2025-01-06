package com.ispan.hestia.dto;

import java.util.Date;

public record ProviderDTO(
        Integer orderId,
        Integer orderRoomId,
        Date orderingDate,
        Date bookedDate,
        Date checkInDate,
        Integer purchasedPrice,
        String userName,
        String roomName,
        String state) {

}
