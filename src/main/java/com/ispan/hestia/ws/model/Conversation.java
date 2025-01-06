package com.ispan.hestia.ws.model;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@Document(collection = "conversation")
public class Conversation {
    
    @Id
    private String id;  
    private Integer user1Id;  
    private Integer user2Id;  
    private LocalDateTime createdAt;  
    private LocalDateTime lastMsgAt;  
    private String lastMsgContent;  
}