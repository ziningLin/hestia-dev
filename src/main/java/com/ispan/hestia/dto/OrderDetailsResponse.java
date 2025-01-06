package com.ispan.hestia.dto;

import java.util.List;

public record OrderDetailsResponse(
    List<OrderDetailsRoomDTO> rooms,
    boolean success,
    String message
) {
    
}
