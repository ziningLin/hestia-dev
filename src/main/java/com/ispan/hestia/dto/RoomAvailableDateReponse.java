package com.ispan.hestia.dto;

import java.util.List;

public record RoomAvailableDateReponse(
                long count,
                List<?> data,
                boolean success,
                String message) {
}
