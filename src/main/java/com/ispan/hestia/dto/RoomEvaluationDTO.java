package com.ispan.hestia.dto;

public record RoomEvaluationDTO(
		Double cleanessTotalScore,
		Double comfortTotalScore,
		Double locationTotalScore,
		Double facilityTotalScore,
		Double pationessTotalScore,
		Double recommendationTotalScore,
		Double overallTotalScore
		) {

}
