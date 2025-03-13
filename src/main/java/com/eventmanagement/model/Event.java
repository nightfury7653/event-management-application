package com.eventmanagement.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "events")
public class Event {
    
    @Id
    private String id;
    private String name;
    private String description;
    private LocalDateTime eventDate;
    private String location;
    private int capacity;
    private EventStatus status;
    private String organizer;
    private String imageUrl;
    private double ticketPrice;
    
    @DBRef
    private List<Registration> registrations = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    public enum EventStatus {
        UPCOMING, ONGOING, COMPLETED, CANCELLED
    }
}