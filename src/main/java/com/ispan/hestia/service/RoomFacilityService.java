package com.ispan.hestia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomFacility;
import com.ispan.hestia.repository.RoomFacilityRepository;

@Service
public class RoomFacilityService {

	@Autowired
	private RoomFacilityRepository roomFacilityRepo;

	// 列出某個房間的所有設施
	public List<RoomFacility> findAllFacilityByRoom(Room room) {
		return roomFacilityRepo.findFacilityByRoom(room);
	}

}
