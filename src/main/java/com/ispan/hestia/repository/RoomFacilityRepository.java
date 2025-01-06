package com.ispan.hestia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Room;
import com.ispan.hestia.model.RoomFacility;

public interface RoomFacilityRepository extends JpaRepository<RoomFacility, Integer> {

    // 列出某個房間的所有設施
    @Query("FROM RoomFacility rf WHERE rf.room = :room")
    List<RoomFacility> findFacilityByRoom(@Param("room") Room room);

}
