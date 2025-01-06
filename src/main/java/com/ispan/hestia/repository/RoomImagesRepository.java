package com.ispan.hestia.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomImages;

public interface RoomImagesRepository extends JpaRepository<RoomImages, Integer> {

	// 列出某個房間的所有照片
	@Query("FROM RoomImages ri WHERE ri.room = :room")
	List<RoomImages> findImagesByRoom(@Param("room") Room room);

	@Query("from RoomImages ri where ri.room.roomId = :roomId")
	Set<RoomImages> findByRoomId(Integer roomId);
	
    @Transactional
    @Modifying
    @Query("DELETE FROM RoomImages ri WHERE ri.room = :room")
    void deleteByRoom(@Param("room") Room room);
}
