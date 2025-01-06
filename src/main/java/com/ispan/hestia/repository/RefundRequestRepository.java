package com.ispan.hestia.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ispan.hestia.dto.OrderProviderRefundRequestDTO;
import com.ispan.hestia.dto.OrderUserRefundRequestDTO;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.Provider;
import com.ispan.hestia.model.RefundRequest;
import com.ispan.hestia.model.State;
import com.ispan.hestia.model.User;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Integer> {
        @Query("Select new com.ispan.hestia.dto.OrderUserRefundRequestDTO(r.refundRequestId, r.date, r.refundReason,  r.totalPriceRefund , s.stateContent , COALESCE(r.order.orderId, r.orderDetails.order.orderId)) from RefundRequest r "
                        +
                        "LEFT JOIN r.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
                        "LEFT JOIN r.orderDetails od " +
                        "JOIN od.roomAvailableDate rad " +
                        " Join r.state s" +
                        " where r.user = :user " +
                        "AND (:startDate IS NULL OR r.date >= :startDate) " +
                        "AND (:endDate IS NULL OR r.date <= :endDate) ORDER BY r.date DESC ")
        public Page<OrderUserRefundRequestDTO> showUserOrderRefundRequest(@Param("user") User user, Pageable pageable,
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        @Query("Select r.refundRequestId, r.date, r.refundReason,  r.totalPriceRefund , s.stateContent , CASE WHEN r.order IS NOT NULL THEN r.order.orderId"
                        + //
                        "  WHEN r.orderDetails IS NOT NULL THEN r.orderDetails.order.orderId " + //
                        " ELSE NULL END AS orderId from RefundRequest r "
                        + "LEFT JOIN r.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
                        "LEFT JOIN r.orderDetails od " +
                        " Join r.state s" +
                        " where r.user = :user")
        public List<Object[]> test(@Param("user") User user);

        // @Query("SELECT new com.ispan.hestia.dto.OrderProviderRefundRequestDTO(" +
        // " rr.refundRequestId, " +
        // " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId), " +
        // " rr.date AS refundDate, " +
        // // " CAST(COALESCE(o.date, od.order.date) AS java.util.Date) AS
        // orderingDate," + // 直接使用 COALESCE 避免
        // // // CASE 對象訪問問題
        // " rr.refundReason, " +
        // " rr.user.userId, " +
        // " rr.user.name, " +
        // " rrp.totalPriceRefund " +
        // ") " +
        // "FROM RefundRequestProvider rrp " +
        // "JOIN rrp.refundRequest rr " +
        // "LEFT JOIN rr.order o " + // LEFT JOIN 支持 `order` 為 null
        // "LEFT JOIN rr.orderDetails od " + // LEFT JOIN 支持 `orderDetails` 為 null
        // "JOIN rr.state s " +
        // "WHERE rrp.provider = :provider")
        // public Page<OrderProviderRefundRequestDTO> showProviderActiveRefundRequest(
        // @Param("provider") Provider provider,
        // Pageable pageable);

        @Modifying
        @Query("Update RefundRequest r set r.state = :postState where r.orderDetails = :orderDetails AND  r.state = :preState")
        public int updateRelatedRefund(@Param("preState") State preState, @Param("postState") State postState,
                        @Param("orderDetails") OrderDetails orderDetails);

        @Query("SELECT new com.ispan.hestia.dto.OrderProviderRefundRequestDTO(" +
                        " rr.refundRequestId, " +
                        " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId), " +
                        " rr.date AS refundDate, " +
                        // " COALESCE(o.date, od.order.date) AS orderingDate, " +
                        " rr.refundReason, " +
                        " rr.user.userId," +
                        " rr.user.name," +
                        " rrp.totalPriceRefund " +
                        ", CASE WHEN rr.order IS NOT NULL THEN true ELSE false END AS refundForOrder" +
                        ",s.stateContent" +
                        // 根據 rr.order
                        // 是否為 null
                        // 設定布林值
                        ") " +
                        "FROM RefundRequestProvider rrp " +
                        "JOIN rrp.refundRequest rr " +
                        "LEFT JOIN rr.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
                        "LEFT JOIN rr.orderDetails od " + // 使用 LEFT JOIN 確保即使 `orderDetails` 為 null也能返回結果
                        "JOIN rr.state s " +
                        "WHERE rrp.provider = :provider " +
                        "AND rrp.state.stateId = 40"
                        +
                        "AND (:startDate IS NULL OR rr.date >= :startDate) " +
                        "AND (:endDate IS NULL OR rr.date <= :endDate) ORDER BY rr.date DESC")
        public Page<OrderProviderRefundRequestDTO> showProviderActiveRefundRequest(@Param("provider") Provider provider,
                        Pageable pageable, @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        @Query("SELECT new com.ispan.hestia.dto.OrderProviderRefundRequestDTO(" +
                        " rr.refundRequestId, " +
                        " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId), " +
                        " rr.date AS refundDate, " +
                        // " COALESCE(o.date, od.order.date) AS orderingDate, " +
                        " rr.refundReason, " +
                        " rr.user.userId," +
                        " rr.user.name," +
                        " rrp.totalPriceRefund " +
                        ", CASE WHEN rr.order IS NOT NULL THEN true ELSE false END AS refundForOrder" +
                        ",s.stateContent" + // 根據 rr.order
                                            // 是否為 null
                                            // 設定布林值
                        ") " +
                        "FROM RefundRequestProvider rrp " +
                        "JOIN rrp.refundRequest rr " +
                        "LEFT JOIN rr.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
                        "LEFT JOIN rr.orderDetails od " + // 使用 LEFT JOIN 確保即使 `orderDetails` 為 null也能返回結果
                        "JOIN rr.state s " +
                        "WHERE rrp.provider = :provider " +
                        "AND (rrp.state.stateId = 41 OR rrp.state.stateId = 42)"
                        +
                        "AND (:startDate IS NULL OR rr.date >= :startDate) " +
                        "AND (:endDate IS NULL OR rr.date <= :endDate) ORDER BY rr.date DESC")
        public Page<OrderProviderRefundRequestDTO> showProviderPastRefundRequest(@Param("provider") Provider provider,
                        Pageable pageable, @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        // @Query("SELECT new com.ispan.hestia.dto.OrderProviderRefundRequestDTO(" +
        // " rr.refundRequestId, " +
        // " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId), " +
        // " rr.date AS refundDate, " +
        // // " COALESCE(o.date, od.order.date) AS orderingDate, " +
        // " rr.refundReason, " +
        // " rr.user.userId," +
        // " rr.user.name," +
        // " rrp.totalPriceRefund " +
        // ", CASE WHEN rr.order IS NOT NULL THEN true ELSE false END AS refundForOrder"
        // + // 根據 rr.order 是否為 null
        // // 設定布林值
        // ") " +
        // "FROM RefundRequestProvider rrp " +
        // "JOIN rrp.refundRequest rr " +
        // "LEFT JOIN rr.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
        // "LEFT JOIN rr.orderDetails od " + // 使用 LEFT JOIN 確保即使 `orderDetails` 為
        // null也能返回結果
        // "JOIN rr.state s " +
        // "WHERE rrp.provider.providerId = :providerId " +
        // "AND rrp.state.stateId = 40")
        // public List<OrderProviderRefundRequestDTO>
        // showProviderActiveRefundRequest(@Param("providerId") Integer providerId);

        @Query(value = "SELECT " +
                        "    rr.refund_request_id AS refundRequestId, " +
                        "    COALESCE(o.order_id, od.order_id) AS orderId, " +
                        "    rr.date AS refundDate, " +
                        "    COALESCE(o.date, odo.date) AS orderingDate, " +
                        "    rr.refund_reason AS refundReason, " +
                        "    rr.user_id AS userId, " +
                        "    u.name AS userName, " +
                        "    rrp.total_price_refund AS totalPriceRefund " +
                        "FROM refund_request_provider rrp " +
                        "JOIN refund_request rr ON rrp.refund_request_id = rr.refund_request_id " +
                        "LEFT JOIN orders o ON rr.order_id = o.order_id " +
                        "LEFT JOIN order_details od ON rr.order_room_id = od.order_room_id " +
                        "LEFT JOIN orders odo ON od.order_id = odo.order_id " +
                        "LEFT JOIN users u ON rr.user_id = u.user_id " +
                        "WHERE rrp.provider_id = :providerId " +
                        "AND rrp.state_id = 40", nativeQuery = true)

        public List<Object[]> test(@Param("providerId") Integer providerId);

        // @Query("SELECT " +
        // " rr.refundRequestId, " +
        // " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId), " +
        // " rr.date AS refundDate, " +
        // " COALESCE(o.date, od.order.date) AS orderingDate, " +
        // " rr.refundReason, " +
        // " rr.user.userId," +
        // " rr.user.name," +
        // " rrp.totalPriceRefund " +

        // "FROM RefundRequestProvider rrp " +
        // "JOIN rrp.refundRequest rr " +
        // "LEFT JOIN rr.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
        // "LEFT JOIN rr.orderDetails od " + // 使用 LEFT JOIN 確保即使 `orderDetails` 為 null
        // 也能返回結果
        // "JOIN rr.state s " +
        // "WHERE rrp.provider.providerId = :providerId " +
        // "AND rrp.state.stateId = 40")
        // public List<Object[]> test(@Param("providerId") Integer providerId);

        // @Query("SELECT " +
        // " rr.refundRequestId, " +

        // " rr.date As refundDate, " +
        // // " CASE " +
        // // " WHEN rr.order IS NOT NULL THEN rr.order.date " +
        // // " ELSE rr.orderDetails.order.date " +
        // // " END AS orderingDate, " +
        // " CASE " +
        // " WHEN rr.order IS NOT NULL THEN rr.order.date " +
        // " ELSE rr.orderDetails.order.date " +
        // " END, " +
        // " rr.refundReason, " +
        // " rr.user.userId," +
        // " rr.user.name," +
        // " rrp.totalPriceRefund, " +
        // " COALESCE(rr.order.orderId, rr.orderDetails.order.orderId) " +

        // "FROM RefundRequestProvider rrp " +
        // "JOIN rrp.refundRequest rr " +
        // "LEFT JOIN rr.order o " + // 使用 LEFT JOIN 確保即使 `order` 為 null 也能返回結果
        // "LEFT JOIN rr.orderDetails od " + // 使用 LEFT JOIN 確保即使 `orderDetails` 為 null
        // 也能返回結果
        // "JOIN rr.state s " +
        // "WHERE rrp.provider.providerId = :providerId " +
        // "AND rrp.state.stateId = 40")
        // public List<Object[]> test(@Param("providerId") Integer providerId);

}
