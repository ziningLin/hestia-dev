package com.ispan.hestia.dto;

import java.util.List;

public record FavoriteResponse(
                long count,
                List<FavoriteDTO> list,
                boolean success,
                String message) {

}
