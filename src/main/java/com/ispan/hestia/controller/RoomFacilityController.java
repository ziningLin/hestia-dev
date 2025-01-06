package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.service.RoomFacilityService;

@RestController
@RequestMapping("/room-facility")
@CrossOrigin
public class RoomFacilityController {

	@Autowired
	private RoomFacilityService roomFacilityService;

	// 列出某個房間的所有設施
	@GetMapping("/{roomId}")
	public List<String> getAllFacilityByRoom(@PathVariable Integer roomId) {
		Room room = new Room();
		room.setRoomId(roomId);
		return roomFacilityService.findAllFacilityByRoom(room).stream()
				.map(roomFacility -> roomFacility.getFacility().getFacilityName()) // 修正點
				.collect(Collectors.toList());
	}

}
