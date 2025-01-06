package com.ispan.hestia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.dao.OrderDAO;
import com.ispan.hestia.dto.OrderProviderMostOrderedRoomsDTO;
import com.ispan.hestia.dto.OrderProviderTopSellingRoomsDTO;
import com.ispan.hestia.dto.OrderReservedRoomDTO;
import com.ispan.hestia.dto.OrderReservedRoomDetailDTO;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.State;

public interface OrderRepository extends JpaRepository<Order, Integer>, OrderDAO {
	@Query("from Order o join o.state s where s.stateId = 30 and o.date < :timeMinusTwoMinutes")
	public List<Order> findUnpaidOrders(@Param("timeMinusTwoMinutes") Date timeMinusTwoMinutes);

	@Query("select date from Order where orderId = :orderId")
	public Date checkOrderDate(@Param("orderId") int orderId);

	@Modifying
	@Query("UPDATE Order o SET o.state = :postState WHERE o.date < :timeMinusTwoMinutes AND o.state = :preState")
	public int updateUnpaidOrderState(@Param("timeMinusTwoMinutes") Date timeMinusTwoMinutes,
			@Param("preState") State preState,
			@Param("postState") State postState);

	@Query("SELECT ('Total', SUM(od.purchasedPrice), COUNT(od.orderRoomId)) " +
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"JOIN od.state s " +
			"WHERE (s.stateId = 38 OR s.stateId = 31) AND p.providerId = :providerId " +
			"AND (:startDate IS NULL OR rad.availableDates >= :startDate) " +
			"AND (:endDate IS NULL OR rad.availableDates <= :endDate)")
	public List<Object[]> getTotalSalesAndOrders(@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("providerId") Integer providerId);

	@Query("Select new com.ispan.hestia.dto.OrderProviderTopSellingRoomsDTO( r.roomId,r.mainImage, r.roomName, SUM(od.purchasedPrice) AS totalSales) "
			+
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"JOIN od.state s " +
			"WHERE (s.stateId = 38 OR s.stateId = 31) AND p.providerId = :providerId " +
			"AND (:startDate IS NULL OR rad.availableDates >= :startDate) " +
			"AND (:endDate IS NULL OR rad.availableDates <= :endDate) " +
			"GROUP BY r.roomId, r.roomName ,r.mainImage " +
			"ORDER BY totalSales DESC")
	public List<OrderProviderTopSellingRoomsDTO> getTopSellingRooms(@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("providerId") Integer providerId, Pageable pageable);

	@Query("SELECT new com.ispan.hestia.dto.OrderProviderMostOrderedRoomsDTO(r.roomId, r.mainImage,r.roomName, COUNT(od.orderRoomId) AS orderCount)  "
			+
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"JOIN od.state s " +
			"WHERE (s.stateId = 38 OR s.stateId = 31) AND p.providerId = :providerId " +
			"AND (:startDate IS NULL OR rad.availableDates >= :startDate) " +
			"AND (:endDate IS NULL OR rad.availableDates <= :endDate) " +
			"GROUP BY r.roomId, r.roomName,r.mainImage " +
			"ORDER BY orderCount DESC")
	public List<OrderProviderMostOrderedRoomsDTO> getMostOrderedRooms(@Param("startDate") Date startDate,
			@Param("endDate") Date endDate,
			@Param("providerId") Integer providerId, Pageable pageable);

	@Query("SELECT new com.ispan.hestia.dto.OrderReservedRoomDTO( " +
			"od.checkInDate AS checkInDate , o.orderId, o.date AS orderingDate, r.roomName AS roomName,r.roomId As roomId, "
			+
			" o.user.userId As userId," +
			" o.user.name AS userName ," +
			" o.user.email AS userContactInfo ," +
			" MIN(rad.availableDates) AS minDate, " +

			"MAX(rad.availableDates) AS maxDate, r.mainImage AS mainImage, COUNT(od.orderRoomId) AS roomCount, " +
			"r.roomAddr AS roomAddr, r.city.cityName AS cityName, r.roomNotice AS roomNotice, " +
			"p.user.userId AS providerUserId ,r.provider.user.name AS providerName, r.provider.user.email AS providerContactInfo ,r.checkinTime AS checkinTime ,r.checkoutTime AS checkoutTime ,r.singleBed AS singleBed,r.doubleBed AS doubleBed,r.bedroomCount AS bedroomCount ,r.bathroom AS bathroom) "
			+
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"JOIN od.state s " +
			"WHERE o.user.userId = :userId " +
			"AND od.checkInDate > :currentTime " +
			"AND s.stateId = 31 " +
			"AND (:startDate IS NULL OR od.checkInDate >= :startDate) " +
			"AND (:endDate IS NULL OR od.checkInDate <= :endDate) " +

			"GROUP BY r.roomId, od.checkInDate, r.roomName, o.orderId, r.mainImage, o.date, r.roomAddr, " +
			"r.city.cityName, r.roomNotice, r.provider.user.name, r.provider.user.email,o.user.name,o.user.email,r.checkinTime,r.checkoutTime,r.singleBed,r.doubleBed,r.bedroomCount,r.bathroom,p.user.userId,o.user.userId  ")
	Page<OrderReservedRoomDTO> findUncompletedRoomOrderUser(@Param("userId") Integer userId,
			@Param("currentTime") Date currentTime,
			Pageable pageable, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT new com.ispan.hestia.dto.OrderReservedRoomDTO( " +
			"od.checkInDate AS checkInDate , o.orderId, o.date AS orderingDate, r.roomName AS roomName,r.roomId As roomId, "
			+
			" o.user.userId As userId," +
			" o.user.name AS userName ," +
			" o.user.email AS userContactInfo ," +
			" MIN(rad.availableDates) AS minDate, " +

			"MAX(rad.availableDates) AS maxDate, r.mainImage AS mainImage, COUNT(od.orderRoomId) AS roomCount, " +
			"r.roomAddr AS roomAddr, r.city.cityName AS cityName, r.roomNotice AS roomNotice, " +
			"p.user.userId AS providerUserId ,r.provider.user.name AS providerName, r.provider.user.email AS providerContactInfo ,r.checkinTime AS checkinTime ,r.checkoutTime AS checkoutTime ,r.singleBed AS singleBed,r.doubleBed AS doubleBed,r.bedroomCount AS bedroomCount ,r.bathroom AS bathroom) "
			+
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"JOIN od.state s " +
			"Where p.providerId  = :providerId AND od.checkInDate>:currentTime AND s.stateId = 31 " +
			"AND (:startDate IS NULL OR od.checkInDate >= :startDate) " +
			"AND (:endDate IS NULL OR od.checkInDate <= :endDate) " +
			"GROUP BY r.roomId, od.checkInDate, r.roomName, o.orderId, r.mainImage, o.date, r.roomAddr, " +
			"r.city.cityName, r.roomNotice, r.provider.user.name, r.provider.user.email,o.user.name,o.user.email,r.checkinTime,r.checkoutTime,r.singleBed,r.doubleBed,r.bedroomCount,r.bathroom,p.user.userId,o.user.userId  ")
	public Page<OrderReservedRoomDTO> findUncompletedRoomOrderProvider(@Param("providerId") Integer providerId,
			@Param("currentTime") Date currentTime, Pageable pageable, @Param("startDate") Date startDate,
			@Param("endDate") Date endDate);

	@Query("SELECT new com.ispan.hestia.dto.OrderReservedRoomDetailDTO (rad.availableDates as availableDate, COUNT(od.orderRoomId) AS roomCount )"
			+
			"FROM Order o " +
			"JOIN o.orderDetails od " +
			"JOIN od.roomAvailableDate rad " +
			"JOIN rad.room r " +
			"JOIN r.provider p " +
			"where r.roomId = :roomId AND o.orderId = :orderId AND od.checkInDate = :checkInDate " +
			"GROUP BY rad.availableDates " +
			"Order by rad.availableDates asc ")
	List<OrderReservedRoomDetailDTO> checkUncompletedRoomOrderDetail(
			@Param("roomId") Integer roomId,
			@Param("orderId") Integer orderId,
			@Param("checkInDate") Date checkInDate);

	@Modifying
	@Query("Update Order o Set o.state.stateId = :stateId where o.orderId = :orderId")
	public int updateOrderState(@Param("orderId") Integer orderId, @Param("stateId") Integer stateId);
}
