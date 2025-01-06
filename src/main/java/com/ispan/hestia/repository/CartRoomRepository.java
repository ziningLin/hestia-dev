package com.ispan.hestia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.CartRoom;
import com.ispan.hestia.repository.impl.CartRoomDAO;

public interface CartRoomRepository extends JpaRepository<CartRoom, Integer>, CartRoomDAO {

	@Query("from CartRoom c where c.user.userId= :uid and c.roomAvailableDate.id= :xxx")
	CartRoom findByUserIdAndRoomAvailableDateId(@Param("uid") Integer userId,
			@Param("xxx") Integer roomAvailableDateId);

	@Query("from CartRoom c where c.user.userId= :uid and c.roomAvailableDate.state.stateId=22")
	List<CartRoom> findByUserId(@Param("uid") Integer userId);

	@Query("from CartRoom c where c.roomAvailableDate.room.roomId= :id")
	List<CartRoom> findByRoomId(@Param("id") Integer roomId);

}
