package com.ispan.hestia.ws.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "message")
public class Message {
	
	@Id
    private String id;  
    private String conversationId;  
    private Integer senderId;  
    private Integer receiverId;  
    private String content; 
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;  
    private boolean isRead;  
	
}
