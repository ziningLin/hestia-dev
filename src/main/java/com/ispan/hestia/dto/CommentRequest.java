package com.ispan.hestia.dto;

public record CommentRequest(
        Integer roomId,
        Integer orderId,
        Integer cleanessScore,
        Integer comfortScore,
        Integer locationScore,
        Integer facilityScore,
        Integer pationessScore,
        Integer recommendationScore,
        Double overallScore,
        String commentContent) {
}
