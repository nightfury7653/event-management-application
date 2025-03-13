package com.eventmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    private String message;
    private NotificationType type;
    private boolean read;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    public enum NotificationType {
        EVENT_REMINDER, REGISTRATION_CONFIRMATION, EVENT_CANCELLED, PAYMENT_CONFIRMATION
    }
}