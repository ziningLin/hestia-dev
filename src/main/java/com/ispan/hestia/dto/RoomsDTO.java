package com.ispan.hestia.dto;

import java.util.Set;

public record RoomsDTO(
        Integer roomId,
        String roomName,
        Integer roomTypeId,
        String roomTypeName,
        Integer cityId,
        String cityName,
        String roomAddr,
        Integer roomSize,
        String roomContent,
        String roomNotice,
        Integer providerId,
        Integer userId,
        String username,
        byte[] photo,
        Integer stateId,
        String stateContent,
        Integer doubleBed,
        Integer singleBed,
        Integer bedroomCount,
        Integer bathroom,
        Double checkinTime,
        Double checkoutTime,
        byte[] mainImage,
        Set<byte[]> roomImages,
        Set<String> roomRegulationName) {
}