package com.ispan.hestia.dto;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderReservedRoomReponse(boolean success,
                String message, Page<OrderReservedRoomDTO> reservedRoom) {

}
