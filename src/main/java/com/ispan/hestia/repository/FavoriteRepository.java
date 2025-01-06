package com.ispan.hestia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Favorite;
import com.ispan.hestia.repository.impl.FavoriteDAO;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer>, FavoriteDAO {

	@Query("from Favorite f where f.user.userId = :uid and f.room.roomId= :rid")
	Favorite findByUserIdandRoomId(@Param("uid") Integer userId, @Param("rid") Integer roomId);

	@Query("from Favorite f where f.user.userId = :uid")
	List<Favorite> findByUserId(@Param("uid") Integer userId);

	@Query("from Favorite f where f.room.roomId = :id")
	Favorite findByRoomId(@Param("id") Integer roomId);
}
