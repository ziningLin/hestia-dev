package com.ispan.hestia.dto;

import java.util.List;

public record CheckOutResponse(
    long count,
    List<OrderDetailsDTO> lst,
    boolean success,
    String message
) {
    
}
