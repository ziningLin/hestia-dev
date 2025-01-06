package com.ispan.hestia.dto;

import java.util.Date;

public record CartRoomDTO(
        Integer cartId,
        Integer userId,
        Integer roomId,
        Date dates,
        Integer price,
        Integer roomSum,
        String roomName,
        Integer size,
        Integer doubleBed,
        Integer singleBed,
        Integer bathroom,
        Integer bedroom,
        Integer cityId,
        String cityName
        ) {

}
