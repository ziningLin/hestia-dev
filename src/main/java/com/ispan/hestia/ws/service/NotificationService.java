package com.ispan.hestia.ws.service;

import java.util.List;

import com.ispan.hestia.ws.model.Notification;

public interface NotificationService {
	
    List<Notification> getAllNotifications(Integer userId);

    void pushNotification(Notification notification);
}
