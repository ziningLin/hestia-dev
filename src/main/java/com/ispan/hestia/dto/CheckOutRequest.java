package com.ispan.hestia.dto;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CheckOutRequest(
    Integer roomId,
    @JsonFormat(shape=JsonFormat.Shape.STRING ,pattern = "yyyy/MM/dd")
    List<Date> dates,
    String promotionCode,
    List<Integer> cartId
) {
    
}
