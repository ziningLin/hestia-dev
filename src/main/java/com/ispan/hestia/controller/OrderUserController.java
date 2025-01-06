package com.ispan.hestia.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.dto.OrderReservedRoomDTO;
import com.ispan.hestia.dto.OrderReservedRoomDetailDTO;
import com.ispan.hestia.dto.OrderReservedRoomReponse;
import com.ispan.hestia.dto.OrderUserDTO;
import com.ispan.hestia.dto.OrderUserRefundRequestDTO;
import com.ispan.hestia.dto.OrderUserReponse;
import com.ispan.hestia.mail.MailManager;
import com.ispan.hestia.service.OrderDetailService;
import com.ispan.hestia.service.OrderMailService;
import com.ispan.hestia.service.RefundRequestService;
import com.ispan.hestia.service.impl.OrderService;
import com.ispan.hestia.util.DateUtil;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin
@RestController
@RequestMapping("/userOrders")
public class OrderUserController {
    @Autowired
    private MailManager mailManager;

    @Autowired
    private OrderMailService orderMailService;

    @Autowired
    private RefundRequestService refReqService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailsService;

    @GetMapping("/findOrder/{id}")
    public OrderUserReponse findOrder(@PathVariable("id") Integer orderId) {
        if (orderId != null) {
            if (orderService.checkIfOrderExist(orderId)) {
                return new OrderUserReponse(true, "查詢成功", null, orderDetailsService.findOrder(orderId), null);
            }
        }
        return new OrderUserReponse(false, "查詢失敗", null, null, null);
    }

    @PostMapping("/reservedRoomDetails")
    public List<OrderReservedRoomDetailDTO> checkUncompletedRoomOrderDetail(@RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Date checkInDate = DateUtil.parseDate(obj.isNull("checkInDate") ? null
                    : obj.getString("checkInDate"), "yyyy-MM-dd");

            Integer roomId = obj.isNull("roomId") ? null : obj.getInt("roomId");
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
            return orderService.findUncompletedOrderDetail(roomId, orderId, checkInDate);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }

    @PostMapping("/findOrders")
    public OrderUserReponse findUserOrders(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");

            Integer userId = (Integer) req.getAttribute("userId");

            System.out.println("userId:" + userId);
            Integer stateId = obj.isNull("stateId") ? null : obj.getInt("stateId");

            String searchInput = obj.isNull("searchInput") ? null : obj.getString("searchInput");

            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            Page<OrderUserDTO> result = orderService.getRoomSum(startSearchDate, endSearchDate, userId,
                    stateId, searchInput, pageNum, pageSize);

            return new OrderUserReponse(true, "查詢成功", result, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, "查詢失敗", null, null, null);
        }
    }

    @PostMapping("/find")
    public OrderUserReponse findUserOrder(HttpServletRequest req, @RequestBody String entity) {
        try {
            JSONObject obj = new JSONObject(entity);
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");

            Integer userId = (Integer) req.getAttribute("userId");
            // Integer userId = obj.isNull("userId") ? null : obj.getInt("userId");

            Integer stateId = obj.isNull("stateId") ? null : obj.getInt("stateId");

            String searchInput = obj.isNull("searchInput") ? null : obj.getString("searchInput");

            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            Page<OrderUserDTO> result = orderService.findUserOrders(startSearchDate, endSearchDate, userId,
                    stateId, searchInput, pageNum, pageSize);

            return new OrderUserReponse(true, "查詢成功", result, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, "查詢失敗", null, null, null);
        }
    }

    @PostMapping("/complete/Check")
    public OrderUserReponse checkIfRoomIsEnough(@RequestBody String entity) {
        // TODO: process POST request

        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
            Integer result = orderService.checkIfRoomIsEnoughForOrder(orderId);
            if (result == 1) {
                return new OrderUserReponse(false, "查詢失敗", null, null, null);
            } else if (result == 2) {
                return new OrderUserReponse(false, "房間數量不足", null, null, null);
            } else {
                return new OrderUserReponse(true, "房間數量足夠", null, null, null);
            }
        } catch (Exception e) {
            // TODO: handle exception
            return new OrderUserReponse(false, "查詢失敗", null, null, null);
        }

    }

    @PostMapping("/complete")
    public String orderComplete(@RequestParam("MerchantTradeNo") String merchantTradeNo,
            @RequestParam("RtnCode") Integer rtnCode) {
        String orderString = merchantTradeNo.substring(1, 7);
        int orderId = Integer.parseInt(orderString);
        System.out.println("merchantTradeNo" + merchantTradeNo);
        System.out.println("orderId" + orderId);
        System.out.println("rtnCode" + rtnCode);
        try {
            if (rtnCode == 1) {
                try {
                    boolean result = orderService.updateOrderStateToSuccess(orderId);
                    System.out.println("orderId" + orderId);
                    if (result) {
                        orderMailService.sendOrderSuccessfullyPaidMail(orderId);
                        return "1|OK";
                    }
                    return "";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @PostMapping("/reserved")
    public OrderReservedRoomReponse getReservedOrderUser(HttpServletRequest req, @RequestBody String entity) {
        try {
System.out.println("reserved有被呼叫");
            JSONObject obj = new JSONObject(entity);
            Integer userId = (Integer) req.getAttribute("userId");
            // Integer userId = obj.isNull("userId") ? null : obj.getInt("userId");
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");
            Page<OrderReservedRoomDTO> reservedRooms = orderService.findUncompletedOrderUser(userId, pageNum,
                    startSearchDate, endSearchDate);
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

    //
    @GetMapping("/refund/check/{id}")
    public OrderUserReponse refundableCheckOrder(@PathVariable("id") Integer orderId) {
        boolean refundable = orderService.checkIfAutoRefundable(orderId);
        String mssg = "不在退款區間內，可以手動申請退款";
        if (refundable) {
            mssg = "在退款區間內，可以直接退款";
        }
        return new OrderUserReponse(refundable, mssg, null, null, null);
    }

    @GetMapping("/refund/check/orderDetails/{id}")
    public OrderUserReponse refundableCheckOrderDetails(@PathVariable("id") Integer orderDetailsId) {
        boolean refundable = orderDetailsService.checkIfAutoRefundable(orderDetailsId);
        String mssg = "不在退款區間內，可以手動申請退款";
        if (refundable) {
            mssg = "在退款區間內，可以直接退款";
        }
        return new OrderUserReponse(refundable, mssg, null, null, null);
    }

    @PostMapping("/refund/autoRefund")
    public OrderUserReponse autoRefund(@RequestBody String entity) {
        String mssg = "退款失敗";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");

            boolean result = orderService.applyAutoRefundOrder(orderId, 31, 36);

            if (result) {

                orderMailService.sendOrderRefundSuccessfullyApprovedMail(orderId);
                mssg = "退款成功";
            }

            return new OrderUserReponse(true, mssg, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, mssg, null, null, null);
        }
    }

    @PostMapping("/refund/autoRefund/Details")
    public OrderUserReponse autoRefundDetails(@RequestBody String entity) {
        String mssg = "退款失敗";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderDetailsId = obj.isNull("orderDetailsId") ? null : obj.getInt("orderDetailsId");

            boolean result = orderDetailsService.applyAutoRefundOrderDetails(orderDetailsId, 36);

            if (result) {
                mssg = "退款成功";
            }

            return new OrderUserReponse(true, mssg, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, mssg, null, null, null);
        }
    }

    @PostMapping("/refund/applyRefund")
    public OrderUserReponse manualRefund(@RequestBody String entity) {
        String mssg = "退款申請送出失敗";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderId = obj.isNull("orderId") ? null : obj.getInt("orderId");
            String refundReason = obj.isNull("refundReason") ? null : obj.getString("refundReason");

            boolean result = orderService.applyRefundOrder(orderId, refundReason);

            if (result) {
                orderMailService.sendOrderRefundRequestCreateMail(orderId);
                mssg = "退款申請成功送出";
            }
            return new OrderUserReponse(result, mssg, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, mssg, null, null, null);
        }
    }

    @PostMapping("/refund/applyRefund/OrderDetails")
    public OrderUserReponse manualRefundOrderDetails(@RequestBody String entity) {
        String mssg = "退款申請送出失敗";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer orderRoomId = obj.isNull("orderRoomId") ? null : obj.getInt("orderRoomId");
            String refundReason = obj.isNull("refundReason") ? null : obj.getString("refundReason");

            boolean result = orderService.applyRefundOrderDetails(orderRoomId, refundReason);
            Integer orderId = orderDetailsService.getOrderId(orderRoomId);
            if (result) {
                orderMailService.sendOrderDetailRefundRequestCreateMail(orderRoomId);
                mssg = "退款申請成功送出";
            }

            return new OrderUserReponse(result, mssg, null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
            return new OrderUserReponse(false, mssg, null, null, null);
        }
    }

    @PostMapping("/refundRequests")
    public OrderUserReponse findUserRefundRequest(HttpServletRequest req, @RequestBody String entity) {
        String mssg = "查詢退款申請失敗ˇ";
        try {
            JSONObject obj = new JSONObject(entity);
            Integer pageNum = obj.isNull("pageNum") ? 0 : obj.getInt("pageNum");
            Integer pageSize = obj.isNull("pageSize") ? 10 : obj.getInt("pageSize");
            Date startSearchDate = DateUtil.parseDate(obj.isNull("startSearchDate") ? null
                    : obj.getString("startSearchDate"), "yyyy-MM-dd");

            Date endSearchDate = DateUtil.parseDate(obj.isNull("endSearchDate") ? null
                    : obj.getString("endSearchDate"), "yyyy-MM-dd");

            System.out.println("startSearchDate" + startSearchDate);

            Integer userId = (Integer) req.getAttribute("userId");
            // Integer userId = obj.isNull("userId") ? null : obj.getInt("userId");
            Page<OrderUserRefundRequestDTO> userRefundRequest = refReqService.findRefundRequestUser(userId, pageNum,
                    pageSize, startSearchDate, endSearchDate);
            if (userRefundRequest == null) {
                return new OrderUserReponse(false, mssg, null, null, null);
            }
            return new OrderUserReponse(true, "查詢成功", null, null, userRefundRequest);
        } catch (Exception e) {
            // TODO: handle exception
            return new OrderUserReponse(false, mssg, null, null, null);
        }
    }
}
