package com.eventmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "feedback")
public class Feedback {
    
    @Id
    private String id;
    
    @DBRef
    private User user;
    
    @DBRef
    private Event event;
    
    private int rating; // 1-5
    private String comment;
    
    @CreatedDate
    private LocalDateTime createdAt;
}