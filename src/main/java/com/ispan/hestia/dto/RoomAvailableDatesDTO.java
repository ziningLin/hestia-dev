package com.ispan.hestia.dto;

import java.util.Date;

public record RoomAvailableDatesDTO(
        Integer id,
        Integer roomId,
        Date availableDates,
        Integer price,
        Integer roomSum,
        String roomName,
        Integer size,
        Integer doubleBed,
        Integer singleBed,
        Integer bathroom,
        Integer bedroom,
        Integer cityId,
        String cityName) {

}
