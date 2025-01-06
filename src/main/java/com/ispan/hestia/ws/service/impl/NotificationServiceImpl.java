package com.ispan.hestia.ws.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ispan.hestia.ws.model.Notification;
import com.ispan.hestia.ws.repository.NotificationRepository;
import com.ispan.hestia.ws.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationRepository notificationRepo;

	@Override
	public List<Notification> getAllNotifications(Integer userId) {
		// TODO: 實作獲取用戶所有通知的邏輯
		return notificationRepo.findAll(); // 可以依需求修改查詢條件
	}

	@Override
	public void pushNotification(Notification notification) {
		// TODO: 實作推送通知的邏輯（包括保存到數據庫並推送給用戶）
		notificationRepo.save(notification);
		// 這裡需要添加推送到 WebSocket 的邏輯
	}

}
