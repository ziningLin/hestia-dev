package com.ispan.hestia.dto;

public record RoomEvaluationResponse(
		RoomEvaluationDTO obj,
		boolean success,
		String message
		) {

}
