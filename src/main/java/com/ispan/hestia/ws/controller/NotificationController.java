package com.ispan.hestia.ws.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ispan.hestia.ws.model.Notification;
import com.ispan.hestia.ws.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notification")
public class NotificationController {
	
    @Autowired
    private NotificationService notificationService;

	/**
	 * 獲取 User 所有通知
	 * @return
	 */
	@GetMapping("/")
	public ResponseEntity<List<Notification>> getAllNotifications(HttpServletRequest req) {
		Integer userId = (Integer) req.getAttribute("userId"); 
        List<Notification> notifications = notificationService.getAllNotifications(userId);
        return ResponseEntity.ok(notifications);
	}
}
