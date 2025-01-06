package com.ispan.hestia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.dto.OrderDetailsDTO;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.State;

public interface OrderDetailsRepository extends JpaRepository<OrderDetails, Integer> {

    @Modifying
    @Query("UPDATE OrderDetails od SET od.state = :postState WHERE od.order.id = :orderId AND od.state = :preState")
    int updateOrderDetailsState(@Param("orderId") Integer orderId,
            @Param("preState") State preState,
            @Param("postState") State postState);

    @Modifying
    @Query("UPDATE OrderDetails od SET od.state = :postState WHERE od.order.date < :timeMinusTwoMinutes AND od.state = :preState")
    int updateUnpaidOrderDetailsState(@Param("timeMinusTwoMinutes") Date timeMinusTwoMinutes,
            @Param("preState") State preState,
            @Param("postState") State postState);

    @Modifying
    @Query("UPDATE OrderDetails od SET od.state = :postState, od.activeRefundRequest=0 WHERE od.order = :order AND od.roomAvailableDate.room.provider = :provider And od.state = :preState")
    int orderRefundProciderApproved(@Param("order") Order order,
            @Param("preState") State preState,
            @Param("postState") State postState,
            @Param("provider") Provider provider);

    // @Query("select od.order.date from OrderDetails od where orderRoomId =
    // :orderRoomId")
    // public Date checkOrderDate(@Param("orderRoomId") int orderRoomId);

    // <<<<<<< HEAD
    // @Query("SELECT new com.ispan.hestia.dto.OrderDetailsDTO(o.id, od.checkInDate,
    // r.roomName, od.purchasedPrice, rad.availableDates as orderedDate,
    // r.singleBed, r.doubleBed, r.bedroomCount, r.checkinTime, r.checkoutTime,
    // od.activeRefundRequest) "
    // =======
    @Query("SELECT new com.ispan.hestia.dto.OrderDetailsDTO(o.orderId, od.orderRoomId, od.checkInDate, r.roomName, od.purchasedPrice,o.date as orderingDate, rad.availableDates as bookededDate, r.singleBed, r.doubleBed, r.bedroomCount, r.checkinTime, r.checkoutTime, s.stateContent,s.stateId, od.activeRefundRequest, r.mainImage,r.roomAddr,r.city.cityName,r.roomNotice,r.provider.user.name as providerName, r.provider.user.email As providerContactInfo ) "
            +
            "FROM Order o JOIN o.orderDetails od " +
            "JOIN od.roomAvailableDate rad " +
            "JOIN rad.room r JOIN od.state s " +
            "WHERE o.orderId = :orderId ")
    List<OrderDetailsDTO> findOrderDetailsByOrderId(@Param("orderId") Integer orderId);

    @Query("from OrderDetails od where od.order.orderId= :id")
    List<OrderDetails> findByOrderId(@Param("id") Integer orderId);

    @Query("from OrderDetails od where od.order.orderId= :orderId and od.roomAvailableDate.room.roomId= :roomId")
    List<OrderDetails> findByOrderIdandRoomId(@Param("orderId") Integer orderId, @Param("roomId") Integer roomId);

    @Modifying
    @Query("Update OrderDetails od " +
            "SET od.state.stateId = 38 " +
            " where od.state.stateId = 31" +
            " AND od.order.orderId = :orderId  " +
            " AND od.checkInDate =  :checkInDate " +
            " AND od.roomAvailableDate.room.roomId = :roomId")
    public int updateOrderDetailsToComplete(@Param("orderId") Integer orderId,
            @Param("roomId") Integer roomId, @Param("checkInDate") Date checkInDate);

}
