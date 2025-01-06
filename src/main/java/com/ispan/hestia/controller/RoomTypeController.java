package com.ispan.hestia.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.RoomTypeDTO;
import com.ispan.hestia.model.RoomType;
import com.ispan.hestia.service.RoomTypeService;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/room-type")
@CrossOrigin
public class RoomTypeController {

	@Autowired
	private RoomTypeService roomTypeService;

	// 列出所有房源類型(住宿類型)
	@GetMapping("/names")
	public List<String> getAllRoomTypesNames() {
		return roomTypeService.findAllRoomType().stream().map(RoomType::getName).collect(Collectors.toList());
	}

	/* 列出所有房源類型 : 含 roomType id */
	@GetMapping("/all")
	public List<RoomTypeDTO> getAllRoomType() {
		List<RoomTypeDTO> roomtypes = roomTypeService.findAllRoomType().stream()
				.map(roomtype -> new RoomTypeDTO(roomtype.getRoomTypeId(), roomtype.getName()))
				.collect(Collectors.toList());
		return roomtypes;
	}

}
