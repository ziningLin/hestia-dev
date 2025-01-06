package com.ispan.hestia.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderUserRefundRequestDTO(
                Integer refundRequestId,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei") Date date,
                String refundReason,
                Integer totalPriceRefund,
                String stateContent,
                Integer orderId) {

}
