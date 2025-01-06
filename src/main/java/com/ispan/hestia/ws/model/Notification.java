package com.ispan.hestia.ws.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "notification")
public class Notification {

	@Id
    private String id;  
    private String userId;  
    private String content;  
    private String category;  // 通知的類型，例如 "ORDER"、"ACCOUNT"
    private boolean isRead;  
    private LocalDateTime createdAt;  
}
