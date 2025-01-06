package com.ispan.hestia.dto;

import java.util.List;

public record CommentResponse(
                long count,
                List<CommentDTO> list,
                boolean success,
                String message) {
}
