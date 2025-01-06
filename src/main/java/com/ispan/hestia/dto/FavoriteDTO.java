package com.ispan.hestia.dto;

public record FavoriteDTO(
        Integer favoriteId,
        Integer userId,
        String roomName,
        String roomAddress,
        Integer roomId,
        Integer doubleBed,
        Integer singleBed,
        Integer bathroom,
        Integer bedroom,
        Integer price,
        Integer size,
        Integer cityId,
        String cityName,
        String note) {

}
