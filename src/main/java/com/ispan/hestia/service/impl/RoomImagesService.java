package com.ispan.hestia.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomImages;
import com.ispan.hestia.repository.RoomImagesRepository;

@Service
public class RoomImagesService {
	@Autowired
	private RoomImagesRepository roomImagesRepo;

	// 用 roomId 查找照片
	public Set<RoomImages> findByRoomId(Integer roomId) {

		if (roomId != null) {
			Set<RoomImages> opt = roomImagesRepo.findByRoomId(roomId);
			if (opt != null && opt.size() != 0) {
				return opt;
			}
		}
		return null;
	}

	// 列出某個房間的所有照片
	public List<RoomImages> findAllImagesByRoom(Room room) {
		return roomImagesRepo.findImagesByRoom(room);
	}

	/* 是否存在 */
	public boolean exists(Integer id) {
		if (id != null) {
			return roomImagesRepo.existsById(id);
		}
		return false;
	}

	/* 刪除一筆 RoomImage */
	public boolean remove(Integer id) {
		if (id != null && roomImagesRepo.existsById(id)) {
			roomImagesRepo.deleteById(id);
			return true;
		}
		return false;
	}
}
