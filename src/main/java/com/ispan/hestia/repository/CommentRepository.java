package com.ispan.hestia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Comment;
import com.ispan.hestia.repository.impl.CommentDAO;

public interface CommentRepository extends JpaRepository<Comment, Integer>, CommentDAO {

	@Query("from Comment c where c.order.orderId = :id")
	List<Comment> findByOrderId(@Param("id") Integer orderRoomId);

	@Query("from Comment c where c.order.user.userId = :id")
	List<Comment> findByUserId(@Param("id") Integer userId);

	@Query("from Comment c where c.room.roomId = :id")
	List<Comment> findByRoomId(@Param("id") Integer roomId);

	@Query("from Comment c where c.room.roomId = :roomId and c.order.orderId = :orderId")
	Comment findByOrderIdAndRoomId(@Param("orderId") Integer orderId, @Param("roomId") Integer roomId);

	@Query("SELECT DISTINCT od.roomAvailableDate.room.roomId FROM OrderDetails od where od.order.orderId = :orderId")
	List<Integer> findDistinctRoomIdsForOrder(@Param("orderId") Integer orderId);

	
}
