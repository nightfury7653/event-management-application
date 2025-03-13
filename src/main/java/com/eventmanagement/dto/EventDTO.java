package com.eventmanagement.dto;

import com.eventmanagement.model.Event;
import lombok.Data;

import java.time.LocalDateTime;

public class EventDTO {
    
    @Data
    public static class Request {
        private String name;
        private String description;
        private LocalDateTime eventDate;
        private String location;
        private int capacity;
        private double ticketPrice;
        private String imageUrl;
    }
    
    @Data
    public static class Response {
        private String id;
        private String name;
        private String description;
        private LocalDateTime eventDate;
        private String location;
        private int capacity;
        private Event.EventStatus status;
        private String organizer;
        private String imageUrl;
        private double ticketPrice;
        private int registrationCount;
        private LocalDateTime createdAt;
    }
}