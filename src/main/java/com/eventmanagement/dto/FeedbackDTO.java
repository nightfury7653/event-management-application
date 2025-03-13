package com.eventmanagement.dto;

import lombok.Data;

public class FeedbackDTO {
    
    @Data
    public static class Request {
        private String eventId;
        private int rating;
        private String comment;
    }
    
    @Data
    public static class Response {
        private String id;
        private String eventId;
        private String eventName;
        private String userId;
        private String userName;
        private int rating;
        private String comment;
    }
}
