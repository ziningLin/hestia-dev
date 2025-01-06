package com.ispan.hestia.dto;

import java.util.List;

public record RoomResponse(
        long count,
        List<?> data,
        boolean success,
        String message) {
}
