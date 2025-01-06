package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderProviderRefundRequestDTO(
        Integer refundRequestId,
        Integer orderId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date refundDate,
        // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
        // Date orderingDate,
        String refundReason,
        Integer userId,
        String name,
        Integer totalPriceRefund,
        boolean refundForOrder,
        String stateContent) {

}
