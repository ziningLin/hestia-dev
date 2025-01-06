package com.ispan.hestia.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.RoomResponse;
import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomImages;
import com.ispan.hestia.service.impl.RoomImagesService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/room-images")
public class RoomImagesController {

	@Autowired
	private RoomImagesService roomImagesService;

	// 用 roomId 取得一張 room 照片
	@GetMapping(path = "/getMainImage/{roomId}", produces = { MediaType.IMAGE_JPEG_VALUE })
	public @ResponseBody byte[] findPhotoByPhotoId(@PathVariable Integer roomId) {

		// 取得所有此 room 的 images
		Set<RoomImages> roomImages = roomImagesService.findByRoomId(roomId);

		RoomImages image = null;
		if (roomImages != null && roomImages.size() != 0) {
			// 取出 Set<RoomImages> 的第一張
			image = new ArrayList<>(roomImages).get(0);
			return image.getImage();
		} else {
			byte[] result = this.photo;
			return result;
		}
	}

	private byte[] photo = null;

	@PostConstruct
	public void initialize() throws IOException {
		byte[] buffer = new byte[8192];

		ClassLoader classLoader = getClass().getClassLoader();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		BufferedInputStream is = new BufferedInputStream(classLoader.getResourceAsStream("static/images/no-image.jpg"));
		int len = is.read(buffer);
		while (len != -1) {
			os.write(buffer, 0, len);
			len = is.read(buffer);
		}
		is.close();
		this.photo = os.toByteArray();
	}

	// 列出某個房間的所有照片
	@GetMapping("/{roomId}")
	public List<byte[]> getAllImagesByRoom(@PathVariable Integer roomId) {
		Room room = new Room();
		room.setRoomId(roomId);
		return roomImagesService.findAllImagesByRoom(room).stream().map(RoomImages::getImage)
				.collect(Collectors.toList());
	}

	/* 刪除一筆 Image */
	@DeleteMapping("/provider/remove/{imageId}")
	public RoomResponse remove(@PathVariable Integer imageId) {
		if (imageId == null) {
			return new RoomResponse(0, null, false, "id是必要欄位");
		} else if (!roomImagesService.exists(imageId)) {
			return new RoomResponse(0, null, false, "id不存在");
		} else {
			if (roomImagesService.remove(imageId)) {
				return new RoomResponse(1, null, true, "刪除成功");
			} else {
				return new RoomResponse(0, null, false, "刪除失敗");
			}
		}
	}
}
