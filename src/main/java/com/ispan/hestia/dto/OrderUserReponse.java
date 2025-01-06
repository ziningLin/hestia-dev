package com.ispan.hestia.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ispan.hestia.model.Order;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderUserReponse(
        boolean success,
        String message,
        Page<OrderUserDTO> userOrders,
        List<OrderDetailsDTO> orderDetails, Page<OrderUserRefundRequestDTO> userRefundRequest) {
}
