package com.ispan.hestia.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomAvailableDate;

public interface RoomAvailableDateRepository extends JpaRepository<RoomAvailableDate, Integer> {

	@Query("SELECT r FROM RoomAvailableDate r WHERE r.room.roomId = :roomId AND r.availableDates BETWEEN :checkInDate AND :checkOutDate")
	List<RoomAvailableDate> findAvailableDatesByRoomIdAndDateRange(@Param("roomId") Integer roomId,
			@Param("checkInDate") Date checkInDate, @Param("checkOutDate") Date checkOutDate);

	@Query("SELECT r FROM RoomAvailableDate r WHERE r.room.roomId = :roomId AND r.availableDates = :date")
	RoomAvailableDate findAvailableDatesByRoomIdAndDate(@Param("roomId") Integer roomId, @Param("date") Date date);

	// 列出某個房間所有可提供日期和價格(已上架)
	@Query("SELECT rad.availableDates, rad.price FROM RoomAvailableDate rad WHERE rad.room.roomId = :roomId and rad.state.stateId =22")
	List<Object[]> findAvailableDatesAndPricesByRoomId(@Param("roomId") Integer roomId);

	Optional<RoomAvailableDate> findByRoomAndAvailableDates(Room room, java.util.Date availableDates);

	/* 多筆查詢: 用 roomId 查詢所有狀態的 Room Available Date */
	@Query("from RoomAvailableDate d where d.room.roomId = :id")
	Set<RoomAvailableDate> findRoomAvailableDateByRoomId(@Param("id") Integer roomId);

	@Query("from RoomAvailableDate d where d.room.roomId = :roomId and d.availableDates = :date")
	Optional<RoomAvailableDate> findAvailableDatesAndPricesByRoomId(@Param("roomId") Integer roomId, @Param("date") Date date);

	@Query("from RoomAvailableDate d where d.room.roomId = :roomId and d.availableDates >= CURRENT_TIMESTAMP and d.availableDates <= :endDate and d.state.stateId =22")
	List<RoomAvailableDate> findRoomAvailableDatesWithinRange(@Param("roomId") Integer roomId, @Param("endDate") Date endDate);

}
