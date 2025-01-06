package com.ispan.hestia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.City;
import com.ispan.hestia.repository.CityRepository;

@Service
public class CityService {

	@Autowired
	private CityRepository cityRepo;

	// 查詢全部城市
	public List<City> findAllCity() {
		return cityRepo.findAll();
	}
}
