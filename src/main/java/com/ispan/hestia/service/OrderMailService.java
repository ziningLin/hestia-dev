package com.ispan.hestia.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.mail.MailManager;
import com.ispan.hestia.model.Order;
import com.ispan.hestia.model.OrderDetails;
import com.ispan.hestia.repository.OrderDetailsRepository;
import com.ispan.hestia.repository.OrderRepository;

@Service
public class OrderMailService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderDetailsRepository orderDetailsRepo;

    @Autowired
    private MailManager mailManager;

    private String getCurrentTime() {
        Date date = new Date();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm");
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String formattedDate = localDateTime.format(formatter);
        return formattedDate;
    }

    private String getUserEmailByOrderId(Integer orderId) {
        Optional<Order> orderOp = orderRepo.findById(orderId);
        if (orderOp.isPresent()) {
            return orderOp.get().getUser().getEmail();
        }
        return null;
    }

    private String getUserEmailByOrderRoomId(Integer orderRoomId) {
        Optional<OrderDetails> orderDetailsOp = orderDetailsRepo.findById(orderRoomId);
        if (orderDetailsOp.isPresent()) {
            return orderDetailsOp.get().getOrder().getUser().getEmail();
        }
        return null;

    }

    private Integer getOrderIdByOrderRoomId(Integer orderRoomId) {
        return orderDetailsRepo.findById(orderRoomId).get().getOrder().getOrderId();
    }

    public void sendOrderSuccessfullyPaidMail(Integer orderId) {

        mailManager.sendNotificationEmail(getUserEmailByOrderId(orderId),
                "Hestia訂房網站 - 付款成功通知", "Hestia訂房網站 - 付款成功通知",
                "親愛的貴賓您好：<br>" + //
                        "<br>" + //
                        "通知您訂單編號[" + orderId + "]的訂單已在" + getCurrentTime() + "成功付款。<br>" + //
                        "如有任何疑問，請與我們的客服進行聯絡。<br>" + //
                        "<br>" + //
                        "祝福您旅途愉快，謝謝！<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }

    public void sendOrderRefundSuccessfullyApprovedMail(Integer orderId) {

        mailManager.sendNotificationEmail(getUserEmailByOrderId(orderId),
                "Hestia訂房網站 - 退款申請通知", "Hestia訂房網站 - 退款申請通知",
                "親愛的貴賓您好：<br>" + //
                        "<br>" + //
                        "通知您為編號為[" + orderId + "]的訂單的退款申請已成功通過。<br>" + //
                        "如有任何疑問，請與我們的客服進行聯絡。<br>" + //
                        "<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }

    public void sendOrderDetailsRefundSuccessfullyApprovedMail(Integer orderRoomId) {

        mailManager.sendNotificationEmail(getUserEmailByOrderRoomId(orderRoomId),
                "Hestia訂房網站 - 退款申請通知", "Hestia訂房網站 - 退款申請通知",
                "親愛的貴賓您好：\r\n" + //
                        "\r\n" + //
                        "通知您為編號為[" + getOrderIdByOrderRoomId(orderRoomId) + "]的訂單中的項目所申請的退款申請已在 " + getCurrentTime()
                        + " 成功通過。<br>" + //
                        "如有任何疑問，請與我們的客服進行聯絡。<br>" + //
                        "<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }

    public void sendOrderRefundRequestCreateMail(Integer orderId) {
        mailManager.sendNotificationEmail(getUserEmailByOrderId(orderId),
                "Hestia訂房網站 - 退款申請通知", "Hestia訂房網站 - 退款申請通知",
                "親愛的貴賓您好：<br>" + //
                        "<br>" + //
                        "通知您已成功在" + getCurrentTime() + "為編號為[" + orderId + "]的訂單申請退款。<br>" + //
                        "如有任何疑問，請與我們的客服進行聯絡。<br>" + //
                        "<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }

    public void sendOrderDetailRefundRequestCreateMail(Integer orderRoomId) {
        mailManager.sendNotificationEmail(getUserEmailByOrderRoomId(orderRoomId),

                "Hestia訂房網站 - 退款申請通知", "Hestia訂房網站 - 退款申請通知",
                "親愛的貴賓您好：<br>" + //
                        "<br>" + //
                        "通知您已成功在" + getCurrentTime() + "為編號為[" + getOrderIdByOrderRoomId(orderRoomId)
                        + "]的訂單中的項目申請退款。\r\n" + //
                        "如有任何疑問，請與我們的客服進行聯絡。\r\n" + //
                        "<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }

    public void sendOrderDetailComplete(Integer orderId) {
        mailManager.sendNotificationEmail(getUserEmailByOrderId(orderId),
                "Hestia訂房網站 - 訂單完成通知", "Hestia訂房網站 - 訂單完成通知",
                "親愛的貴賓您好：<br>" + //
                        "<br>" + //
                        "通知您賣家已在" + getCurrentTime() + "完成編號為[" + orderId + "]的訂單中的項目。<br>" + //
                        "如有任何疑問，請與我們的客服進行聯絡。<br>" + //
                        "<br>" + //
                        "--<br>" + //
                        "<br>" + //
                        "此信件為自動發送，請勿直接回覆，謝謝。",
                null, null);
    }
}
