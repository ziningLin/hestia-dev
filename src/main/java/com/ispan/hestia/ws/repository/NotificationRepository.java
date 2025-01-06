package com.ispan.hestia.ws.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ispan.hestia.ws.model.Notification;

public interface NotificationRepository extends MongoRepository<Notification, String>{

}
