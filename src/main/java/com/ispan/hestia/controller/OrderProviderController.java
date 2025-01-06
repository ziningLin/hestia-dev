package com.ispan.hestia.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.OrderProviderDTO;
import com.ispan.hestia.dto.OrderProviderRefundRequestDTO;
import com.ispan.hestia.dto.OrderProviderReponse;
import com.ispan.hestia.dto.OrderReservedRoomDTO;
import com.ispan.hestia.dto.OrderReservedRoomDetailDTO;
import com.ispan.hestia.dto.OrderReservedRoomReponse;
import com.ispan.hestia.service.OrderDetailService;
import com.ispan.hestia.service.OrderMailService;
import com.ispan.hestia.service.RefundRequestService;
import com.ispan.hestia.service.impl.OrderService;
import com.ispan.hestia.util.DateUtil;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/providerOrders")
public class OrderProviderController {
    @Autowired
    private OrderMailService orderMailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private RefundRequestService refReqService;

    @PostMapping("/reserved")
    public OrderReservedRoomReponse getReservedOrderProvider(HttpServletRequest req, @RequestBody String entity) {
        try {

            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");
            Page<OrderReservedRoomDTO> reservedRooms = orderService.findUncompletedOrderProvider(providerId,
                    pageNum, startSearchDate, endSearchDate);

            if (reservedRooms != null) {
                return new OrderReservedRoomReponse(true, "查詢成功", reservedRooms);
            }
            System.out.println("是null");
            return new OrderReservedRoomReponse(false, "查詢失敗", null);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("壞掉");
            return new OrderReservedRoomReponse(false, "查詢失敗", null);
        }
    }

    @PostMapping("/refundRequestsOD/Approve")
    public OrderProviderReponse approveRefundRequestOD(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getInt("providerId");
            Integer refundReqId = obj.isNull("refundReqId") ? null : obj.getInt("refundReqId");
            Integer stateId = 41;
            if (orderService.approveOrDenialOrderDetailRefund(providerId, refundReqId, stateId)) {
                return new OrderProviderReponse(true, "退款成功通過申請", null, null, null, null, null);
            }
            System.out.println("壞掉1");
            return new OrderProviderReponse(false, "接受退款申請失敗", null, null, null, null, null);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("壞掉2");
            return new OrderProviderReponse(false, "拒絕退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/refundRequests/Approve")
    public OrderProviderReponse approveRefundRequest(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn
            Integer refundReqId = obj.isNull("refundReqId") ? null : obj.getInt("refundReqId");
            Integer stateId = 41;
            Integer orderId = refReqService.getOrderIdFromRefReqId(refundReqId);
            if (orderService.approveOrDenialOrderRefund(providerId, refundReqId, stateId)) {
                orderMailService.sendOrderRefundSuccessfullyApprovedMail(orderId);
                return new OrderProviderReponse(true, "退款成功通過申請", null, null, null, null, null);
            }

            return new OrderProviderReponse(false, "接受退款申請失敗", null, null, null, null, null);
        } catch (Exception e) {
            return new OrderProviderReponse(false, "接受退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/refundRequestsOD/Decline")
    public OrderProviderReponse declineRefundRequestOD(HttpServletRequest req, @RequestBody String entity) {
        System.out.println("我有被呼叫");
        try {
            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn
            Integer refundReqId = obj.isNull("refundReqId") ? null : obj.getInt("refundReqId");
            Integer stateId = 42;
            if (orderService.approveOrDenialOrderDetailRefund(providerId, refundReqId, stateId)) {
                return new OrderProviderReponse(true, "成功拒絕退款申請", null, null, null, null, null);
            }
            System.out.println("壞掉1");
            return new OrderProviderReponse(false, "拒絕退款申請失敗", null, null, null, null, null);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("壞掉2");
            return new OrderProviderReponse(false, "拒絕退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/refundRequests/Decline")
    public OrderProviderReponse declineRefundRequest(HttpServletRequest req, @RequestBody String entity) {
        System.out.println("我有被呼叫");
        try {
            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn
            Integer refundReqId = obj.isNull("refundReqId") ? null : obj.getInt("refundReqId");
            Integer stateId = 42;
            if (orderService.approveOrDenialOrderRefund(providerId, refundReqId, stateId)) {
                return new OrderProviderReponse(true, "成功拒絕退款申請", null, null, null, null, null);
            }

            return new OrderProviderReponse(false, "拒絕退款申請失敗", null, null, null, null, null);
        } catch (Exception e) {
            return new OrderProviderReponse(false, "拒絕退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/refundRequests")
    public OrderProviderReponse findProviderRefundRequest(HttpServletRequest req, @RequestBody String entity) {
        // String mssg = "查詢退款申請失敗ˇ";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");

            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn
            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            Page<OrderProviderRefundRequestDTO> providerRefundRequest = refReqService.activeRefundRequestProvider(
                    providerId, pageNum,
                    pageSize, startSearchDate, endSearchDate);
            return new OrderProviderReponse(true, "查詢退款申請成功", null, null, providerRefundRequest, null, null);
        } catch (Exception e) {
            // TODO: handle exception
            return new OrderProviderReponse(false, "查詢退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/pastRefundRequests")
    public OrderProviderReponse findProviderPastRefundRequest(HttpServletRequest req, @RequestBody String entity) {
        // String mssg = "查詢退款申請失敗ˇ";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");

            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn

            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            Page<OrderProviderRefundRequestDTO> providerRefundRequest = refReqService.pastRefundRequestProvider(
                    providerId, pageNum,
                    pageSize, startSearchDate, endSearchDate);
            return new OrderProviderReponse(true, "查詢退款申請成功", null, null, providerRefundRequest, null, null);
        } catch (Exception e) {
            // TODO: handle exception
            return new OrderProviderReponse(false, "查詢退款申請失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/find")
    public OrderProviderReponse findProviderOrder(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);

            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");
            String searchInput = obj.isNull("searchInput") ? null : obj.getString("searchInput");

            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn

            Integer stateId = obj.isNull("stateId") ? null : obj.getInt("stateId");

            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            Page<OrderProviderDTO> result = orderService.findProviderOrders(startSearchDate, endSearchDate,
                    providerId,
                    stateId, searchInput, pageNum,
                    pageSize);

            return new OrderProviderReponse(true, "查詢成功", null, result, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OrderProviderReponse(false, "查詢失敗", null, null, null, null, null);
        }
    }

    @PostMapping("/salesNumber")
    public OrderProviderReponse checkSalesNumber(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer providerId = orderService.getProviderId((Integer) req.getAttribute("userId"));
            // Integer providerId = obj.isNull("providerId") ? null :
            // obj.getIn

            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");
            return new OrderProviderReponse(true, "查詢成功",
                    orderService.getMonthlySalesAndOrders(startSearchDate, endSearchDate, providerId), null, null,
                    orderService.getTopSellingRooms(startSearchDate, endSearchDate, providerId),
                    orderService.getMostOrderedRooms(startSearchDate, endSearchDate, providerId));

        } catch (Exception e) {
            System.out.println("壞掉");
            e.printStackTrace();
            return new OrderProviderReponse(false, "查詢失敗", null, null, null, null, null);

        }

    }

    @PostMapping("/reservedRoomDetails")
    public List<OrderReservedRoomDetailDTO> checkUncompletedRoomOrderDetail(@RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");

            Date checkInDate = DateUtil.parseDate(obj.isNull("checkInDate") ? null
                    : obj.getString("checkInDate"), "yyyy-MM-dd");

            return orderService.findUncompletedOrderDetail(roomId, orderId, checkInDate);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }

    @PostMapping("/orderComplete")
    public OrderProviderReponse updateOrderDetailsToComplete(@RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);

            Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");

            Date checkInDate = DateUtil.parseDate(obj.isNull("checkInDate") ? null
                    : obj.getString("checkInDate"), "yyyy-MM-dd");
            System.out.println("checkInDate" + checkInDate);
            System.out.println("orderId" + orderId);
            System.out.println("roomId" + roomId);
            orderService.updateOrderAndCheckCompletion(orderId, roomId, checkInDate);
            orderMailService.sendOrderDetailComplete(orderId);
            return new OrderProviderReponse(true, "更新成功",
                    null, null, null,
                    null,
                    null);

        } catch (Exception e) {
            System.out.println("壞掉");
            e.printStackTrace();
            return new OrderProviderReponse(false, "更新失敗", null, null, null, null, null);

        }
    }
}
