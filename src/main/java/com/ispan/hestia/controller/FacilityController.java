package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.CityDTO;
import com.ispan.hestia.dto.FacilityDTO;
import com.ispan.hestia.model.Facility;
import com.ispan.hestia.service.FacilityService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/facility")
@CrossOrigin
public class FacilityController {

	@Autowired
	private FacilityService facilityService;

	// 列出所有設施
	@GetMapping("/names")
	public List<String> getAllFacilities() {
		return facilityService.findAllFacility().stream().map(Facility::getFacilityName).collect(Collectors.toList());
	}

	/* 列出所有設施：含 facility id */
	@GetMapping("/all")
	public List<FacilityDTO> getAllFacility() {
		List<FacilityDTO> facilities = facilityService.findAllFacility().stream()
				.map(facility -> new FacilityDTO(facility.getFacilityId(), facility.getFacilityName()))
				.collect(Collectors.toList());
		return facilities;
	}

}
