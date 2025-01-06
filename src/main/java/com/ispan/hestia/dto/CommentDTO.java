package com.ispan.hestia.dto;

import java.util.Date;

public record CommentDTO(
        Integer commentId,
        Integer orderId,
        Integer roomId,
        String userName,
        byte[] userPhoto,
        Date commentDate,
        Integer cleanessScore,
        Integer comfortScore,
        Integer locationScore,
        Integer facilityScore,
        Integer pationessScore,
        Integer recommendationScore,
        Double overallScore,
        Integer useful,
        String commentContent) {

}
