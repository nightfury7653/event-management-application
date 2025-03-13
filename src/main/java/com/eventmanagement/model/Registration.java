package com.eventmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "registrations")
public class Registration {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private Event event;
    
    private LocalDateTime registrationDate;
    private RegistrationStatus status;
    private String paymentId;
    private double amountPaid;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum RegistrationStatus {
        PENDING, CONFIRMED, CANCELLED, ATTENDED
    }
}