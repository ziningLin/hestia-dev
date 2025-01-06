package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.CityDTO;
import com.ispan.hestia.model.City;
import com.ispan.hestia.service.CityService;

@RestController
@RequestMapping("/city")
@CrossOrigin
public class CityController {

	@Autowired
	private CityService cityService;

	// 列出所有縣市
	@GetMapping("/names")
	public List<String> getAllCityNames() {
		return cityService.findAllCity().stream().map(City::getCityName).collect(Collectors.toList());
	}

	/* 列出所有縣市：含 city id */
	@GetMapping("/all")
	public List<CityDTO> getAllCity() {
		List<CityDTO> cities = cityService.findAllCity().stream()
				.map(city -> new CityDTO(city.getCityId(), city.getCityName()))
				.collect(Collectors.toList());
		return cities;
	}
}
