package com.ispan.hestia.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Promotion;
import com.ispan.hestia.model.PromotionRooms;
import com.ispan.hestia.model.Room;

public interface PromotionRoomRepository extends JpaRepository<PromotionRooms, Integer> {

	@Query("SELECT pr.promotion FROM PromotionRooms pr WHERE pr.room.roomId = :roomId")
	List<Promotion> findPromotionsByRoomId(@Param("roomId") Integer roomId);
	
	@Query("SELECT pr.promotion.id FROM PromotionRooms pr WHERE pr.room.id = :roomId")
	List<Integer> findPromotionIdsByRoomId(@Param("roomId") Integer roomId);

	boolean existsByRoomAndPromotion(Room room, Promotion promotion);
	
	@Modifying
	@Query("DELETE FROM PromotionRooms pr WHERE pr.room.id = :roomId AND pr.promotion.id IN :promotionIds")
	void deleteAllByRoomIdAndPromotionIds(@Param("roomId") Integer roomId, @Param("promotionIds") Set<Integer> promotionIds);
}
