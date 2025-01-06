package com.ispan.hestia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.RoomType;
import com.ispan.hestia.repository.RoomTypeRepository;

@Service
public class RoomTypeService {

	@Autowired
	private RoomTypeRepository roomTypeRepo;

	// 查詢全部房源類型(住宿類型)
	public List<RoomType> findAllRoomType() {
		return roomTypeRepo.findAll();
	}

}
