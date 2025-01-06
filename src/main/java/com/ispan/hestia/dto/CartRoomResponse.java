package com.ispan.hestia.dto;

import java.util.List;

public record CartRoomResponse(
        long count,
        List<CartRoomDTO> lst,
        boolean success,
        String message) {
}
