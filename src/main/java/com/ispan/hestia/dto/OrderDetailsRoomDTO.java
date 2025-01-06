package com.ispan.hestia.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderDetailsRoomDTO(
        Integer roomId,
        String roomName,
        Date startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Taipei") Date endDate,
        Integer roomSize,
        Integer bedroom) {

}
