package com.eventmanagement.dto;

import lombok.Data;
import com.eventmanagement.model.Registration;

import java.time.LocalDateTime;

public class RegistrationDTO {
    
    @Data
    public static class Request {
        private String eventId;
        private String paymentId;
    }
    
    @Data
    public static class Response {
        private String id;
        private String eventId;
        private String eventName;
        private String userId;
        private String userName;
        private LocalDateTime registrationDate;
        private Registration.RegistrationStatus status;
        private String paymentId;
        private double amountPaid;
    }
}
