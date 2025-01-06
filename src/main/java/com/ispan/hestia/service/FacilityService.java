package com.ispan.hestia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.Facility;
import com.ispan.hestia.repository.FacilityRepository;

@Service
public class FacilityService {

	@Autowired
	private FacilityRepository facilityRepo;

	// 查詢全部設施
	public List<Facility> findAllFacility() {
		return facilityRepo.findAll();
	}
}
