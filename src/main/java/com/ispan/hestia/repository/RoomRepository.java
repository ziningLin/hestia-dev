package com.ispan.hestia.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.model.Room;

public interface RoomRepository extends JpaRepository<Room, Integer> {
	// 查詢已上架的全部房間
	@Query(value = "FROM Room r WHERE r.state.stateId=22")
	List<Room> findRoom();

	// 根據房間id查詢(已上架)
	@Query(value = "FROM Room r WHERE roomId= :id AND r.state.stateId=22")
	List<Room> findRoomByRoomId(@Param("id") Integer roomId);

	// 根據房間名稱模糊查詢(已上架)
	@Query(value = "FROM Room r WHERE roomName LIKE %:n% AND r.state.stateId=22")
	List<Room> findRoomByRoomNameLike(@Param("n") String roomName);

	// 根據房源類型(住宿類型)查詢(已上架)
	@Query(value = "FROM Room r WHERE r.roomType.name= :t AND r.state.stateId=22")
	List<Room> findRoomByRoomType(@Param("t") String roomTypeName);

	// 根據縣市名稱查詢(已上架)
	@Query(value = "FROM Room r WHERE r.city.cityName= :c AND r.state.stateId=22")
	List<Room> findRoomByCity(@Param("c") String cityName);

	// 根據地址模糊查詢(已上架)
	@Query(value = "FROM Room r WHERE roomAddr LIKE %:a% AND r.state.stateId=22")
	List<Room> findRoomByRoomAddrLike(@Param("a") String roomAddr);

	// 根據入住人數查詢(已上架)
	@Query(value = "FROM Room r WHERE roomSize >= :s AND r.state.stateId=22")
	List<Room> findRoomByRoomSizeGreaterThanOrEqual(@Param("s") Integer roomSize);

	// 根據雙人床數量查詢(已上架)
	@Query(value = "FROM Room r WHERE doubleBed >= :double AND r.state.stateId=22")
	List<Room> findRoomByDoubleBedGreaterThanOrEqual(@Param("double") Integer doubleBed);

	// 根據單人床數量查詢(已上架)
	@Query(value = "FROM Room r WHERE singleBed >= :single AND r.state.stateId=22")
	List<Room> findRoomBySingleBedGreaterThanOrEqual(@Param("single") Integer singleBed);

	// 根據臥室數量查詢(已上架)
	@Query(value = "FROM Room r WHERE bedroomCount >= :bed AND r.state.stateId=22")
	List<Room> findRoomByBedroomGreaterThanOrEqual(@Param("bed") Integer bedroomCount);

	// 根據衛浴數量查詢(已上架)
	@Query(value = "FROM Room r WHERE bathroom >= :bath AND r.state.stateId=22")
	List<Room> findRoomByBathroomGreaterThanOrEqual(@Param("bath") Integer bathroom);

	// 根據價格範圍查詢所有符合的房間(已上架)
	@Query("SELECT DISTINCT r FROM Room r JOIN r.roomAvailableDate rad WHERE (rad.price BETWEEN :minPrice AND :maxPrice) AND r.state.stateId=22")
	List<Room> findRoomByPriceRange(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice);

	// 根據日期範圍查詢所有符合的房間(已上架)
	@Query(value = "SELECT DISTINCT r.* FROM room r " + "WHERE EXISTS (" + "SELECT 1 FROM room_available_date rad "
			+ "WHERE rad.room_id = r.room_id " + "AND r.state_id=22" + "AND rad.available_dates >= :checkinDate"
			+ " AND rad.available_dates < :checkoutDate " + "GROUP BY rad.room_id "
			+ "HAVING COUNT(rad.available_dates) >= "
			+ "DATEDIFF(DAY, :checkinDate, :checkoutDate))", nativeQuery = true)
	List<Room> findRoomByDateRange(@Param("checkinDate") Date checkinDate, @Param("checkoutDate") Date checkoutDate);

	// 根據所選設施查詢所有符合的房間(已上架)
	@Query("SELECT r FROM Room r " + "JOIN r.roomFacility rf " + "JOIN rf.facility f "
			+ "WHERE f.facilityName IN :facilityNames AND r.state.stateId=22" + "GROUP BY r "
			+ "HAVING COUNT(DISTINCT f.facilityName) = :facilityCount")
	List<Room> findRoomByFacilities(@Param("facilityNames") List<String> facilityNames,
			@Param("facilityCount") Integer facilityCount);

	// 根據房間名稱 or 縣市 or 地址 or 入住人數 or 日期範圍查詢(已上架)
	@Query(value = "SELECT DISTINCT r.* FROM room r " + "LEFT JOIN room_available_date rad ON r.room_id = rad.room_id "
			+ "LEFT JOIN city c ON r.city_id = c.city_id " + "WHERE (:keyword IS NULL OR "
			+ "r.room_name LIKE CONCAT('%', :keyword, '%') OR " + "c.city_name LIKE CONCAT('%', :keyword, '%') OR "
			+ "r.room_addr LIKE CONCAT('%', :keyword, '%')) " + "AND (:roomSize IS NULL OR r.room_size >= :roomSize) "
			+ "AND ((:checkinDate IS NULL AND :checkoutDate IS NULL) OR "
			+ "(:checkinDate IS NOT NULL AND :checkoutDate IS NOT NULL AND "
			+ "rad.available_dates >= :checkinDate AND rad.available_dates < :checkoutDate)) "
			+ "AND EXISTS (SELECT 1 FROM room_available_date radSub " + "WHERE radSub.room_id = r.room_id "
			+ "AND r.state_id=22" + "AND radSub.available_dates >= :checkinDate "
			+ "AND radSub.available_dates < :checkoutDate " + "GROUP BY radSub.room_id "
			+ "HAVING COUNT(radSub.available_dates) >= "
			+ "DATEDIFF(DAY, :checkinDate, :checkoutDate))", nativeQuery = true)
	List<Room> findRoomBySearchBar(@Param("keyword") String keyword, @Param("roomSize") Integer roomSize,
			@Param("checkinDate") Date checkinDate, @Param("checkoutDate") Date checkoutDate);

	// 根據價格範圍 or 雙人床數量 or 單人床數量 or 臥室數量 or 衛浴數量 or 所選設施查詢(已上架)
	@Query("SELECT DISTINCT r FROM Room r " + "LEFT JOIN r.roomFacility rf " + "LEFT JOIN rf.facility f "
			+ "LEFT JOIN r.roomAvailableDate rad "
			+ "WHERE (:minPrice IS NULL AND :maxPrice IS NULL) OR (rad.price BETWEEN :minPrice AND :maxPrice) "
			+ "AND (:doubleBed IS NULL OR r.doubleBed >= :doubleBed) "
			+ "AND (:singleBed IS NULL OR r.singleBed >= :singleBed) "
			+ "AND (:bedroomCount IS NULL OR r.bedroomCount >= :bedroomCount) "
			+ "AND (:bathroom IS NULL OR r.bathroom >= :bathroom) "
			+ "AND (:facilityNames IS NULL OR f.facilityName IN :facilityNames) AND r.state.stateId=22 " + "GROUP BY r "
			+ "HAVING (:facilityNames IS NULL OR COUNT(DISTINCT f.facilityName) = :facilityCount)")
	List<Room> findRoomByFilter(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice,
			@Param("doubleBed") Integer doubleBed, @Param("singleBed") Integer singleBed,
			@Param("bedroomCount") Integer bedroomCount, @Param("bathroom") Integer bathroom,
			@Param("facilityNames") List<String> facilityNames, @Param("facilityCount") Integer facilityCount);

	@Query("FROM Room r WHERE provider.providerId = :providerId")
	Set<Room> findRoomByProviderId(Integer providerId);
}