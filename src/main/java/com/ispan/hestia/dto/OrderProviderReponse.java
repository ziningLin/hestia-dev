package com.ispan.hestia.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderProviderReponse(
        boolean success,
        String message,
        SalesNumbersSumDTO salesNumbers,
        Page<OrderProviderDTO> providerOrders,
        Page<OrderProviderRefundRequestDTO> activeRefundRequestPage,
        List<OrderProviderTopSellingRoomsDTO> topSellingRooms,
        List<OrderProviderMostOrderedRoomsDTO> mostOrderedRooms) {

}
