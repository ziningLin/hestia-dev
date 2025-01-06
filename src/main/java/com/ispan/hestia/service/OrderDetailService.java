package com.ispan.hestia.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ispan.hestia.dto.OrderDetailsDTO;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.model.OrderDetailsRefundRecord;
import com.ispan.hestia.model.RefundRequest;
import com.ispan.hestia.model.State;
import com.ispan.hestia.repository.OrderDetailsRefundRecordRepository;
import com.ispan.hestia.repository.OrderDetailsRepository;
import com.ispan.hestia.repository.OrderRepository;
import com.ispan.hestia.repository.RefundRequestRepository;
import com.ispan.hestia.repository.StateRepository;

@Service
public class OrderDetailService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderDetailsRepository orderDetailsRepo;

    @Autowired
    private StateRepository stateRepo;

    @Autowired
    private OrderDetailsRefundRecordRepository odrrRepo;

    @Autowired
    private RefundRequestRepository refReqRepo;

    public Integer getOrderId(Integer orderRoomId) {
        return orderDetailsRepo.findById(orderRoomId).get().getOrder().getOrderId();
    }

    @Transactional
    public boolean checkIfAutoRefundable(Integer orderRoomId) {
        Date currentTime = new Date();
        // Date orderDate = new
        // Date(orderDetailsRepo.checkOrderDate(orderRoomId).getTime() + 5 * 60 *
        // 1000);// orderDate
        // 找到訂單時間然後加5分鐘再去比較
        Date orderDetailsDate = orderDetailsRepo.findById(orderRoomId).get().getOrder().getDate();

        Date orderDate = new Date(orderDetailsDate.getTime() + 5 * 60 * 1000);
        if (orderDate.compareTo(currentTime) < 0) {
            System.out.println("orderDate" + orderDate);
            System.out.println("currentTime" + currentTime);
            return false;
        }
        System.out.println("orderDate" + orderDate);
        System.out.println("currentTime" + currentTime);
        return true;
    }

    @Transactional // 修改訂單狀態
    public boolean applyAutoRefundOrderDetails(Integer orderRoomId, Integer postStateId) {
        Optional<OrderDetails> odOptional = orderDetailsRepo.findById(orderRoomId);
        if (odOptional.isEmpty()) {
            return false;
        }
        OrderDetails orderDetails = odOptional.get();
        // State successState = stateRepo.findById(31).get();// 找到完成的狀態
        State postState = stateRepo.findById(postStateId).get();// 找到退款中的狀態
        boolean stateCheck = true;
        Order order = orderDetails.getOrder();
        try {

            RefundRequest refundRequest = new RefundRequest();

            State refundRequestState = stateRepo.findById(41).get();// 退款申請 通過 狀態

            refundRequest.setOrderDetails(orderDetails);
            refundRequest.setDate(new Date());
            refundRequest.setState(refundRequestState);
            refundRequest.setTotalPriceRefund(orderDetails.getPurchasedPrice());
            refundRequest.setUser(order.getUser());

            orderDetails.setState(postState);
            for (OrderDetails singleOrderDetail : order.getOrderDetails()) {
                if (!singleOrderDetail.getState().equals(postState)) {
                    stateCheck = false;
                    break;
                }

            }
            if (stateCheck) {
                order.setState(postState);
                System.out.println("postState set");
            }
            if (postStateId == 31) {
                System.out.println("postStateId == 31");
                OrderDetailsRefundRecord odrr = new OrderDetailsRefundRecord();
                odrr.setOrderDetails(orderDetails);
                odrr.setDate(new Date());
                odrrRepo.save(odrr);
            }
            return true;

        } catch (Exception e) {
            System.out.println("出錯了");
            e.printStackTrace();
            return false;
        }
    }

    // @Transactional
    // public boolean autoRefundOrderDetails(Integer orderRoomId) { // 手動退款也可以用這個
    // Optional<OrderDetails> odOptional = orderDetailsRepo.findById(orderRoomId);
    // if (odOptional.isEmpty()) {
    // return false;
    // }
    // OrderDetails orderDetails = odOptional.get();
    // // State successState = stateRepo.findById(31).get();// 找到完成的狀態
    // State refundingState = stateRepo.findById(35).get();// 找到退款中的狀態
    // boolean stateCheck = true;
    // Order order = orderDetails.getOrder();
    // try {
    // orderDetails.setState(refundingState);
    // for (OrderDetails singleOrderDetail : order.getOrderDetails()) {
    // if (!singleOrderDetail.getState().equals(refundingState)) {
    // stateCheck = false;
    // break;
    // }

    // }
    // if (stateCheck) {
    // order.setState(refundingState);
    // }
    // return true;

    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }

    // }

    // @Transactional
    // public boolean manualRefundOrderDetail(Integer orderRoomId) {
    // try {
    // Optional<OrderDetails> odOptional = orderDetailsRepo.findById(orderRoomId);
    // if (odOptional.isEmpty()) {
    // return false;
    // }
    // OrderDetails orderDetails = odOptional.get();
    // // State successState = stateRepo.findById(31).get();// 找到完成的狀態
    // State applyingRefundState = stateRepo.findById(34).get();// 找到申請退款的狀態
    // boolean stateCheck = true;
    // Order order = orderDetails.getOrder();
    // for (OrderDetails singleOrderDetail : order.getOrderDetails()) {
    // if (!singleOrderDetail.getState().equals(applyingRefundState)) {
    // stateCheck = false;
    // break;
    // }
    // }
    // if (stateCheck) {
    // order.setState(applyingRefundState);
    // }
    // return true;

    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }
    // }

    // @Transactional // 申請退款 手動拒絕退款
    // public boolean manualRefundDeclinedOrderDetails(Integer orderRoomId) {
    // try {
    // Optional<OrderDetails> odOptional = orderDetailsRepo.findById(orderRoomId);
    // if (odOptional.isEmpty()) {
    // return false;
    // }
    // OrderDetails orderDetails = odOptional.get();
    // // State successState = stateRepo.findById(31).get();// 找到完成的狀態
    // State successState = stateRepo.findById(31).get();// 把狀態變回成功
    // boolean stateCheck = true;
    // Order order = orderDetails.getOrder();
    // for (OrderDetails singleOrderDetail : order.getOrderDetails()) {
    // if (!singleOrderDetail.getState().equals(successState)) {
    // stateCheck = false;
    // break;
    // }
    // }
    // if (stateCheck) {
    // order.setState(successState);
    // }

    // return true;

    // } catch (Exception e) {
    // e.printStackTrace();
    // return false;
    // }
    // }

    @Transactional
    public List<OrderDetailsDTO> findOrder(Integer orderId) {
        return orderDetailsRepo.findOrderDetailsByOrderId(orderId);
    }

}
