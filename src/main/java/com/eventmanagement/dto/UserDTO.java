package com.eventmanagement.dto;

import lombok.Data;
import com.eventmanagement.model.User;
import java.util.Set;

public class UserDTO {
    
    @Data
    public static class Request {
        private String name;
        private String email;
        private String password;
        private String phoneNumber;
    }
    
    @Data
    public static class Response {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;
        private Set<User.Role> roles;
    }
    
    @Data
    public static class AuthRequest {
        private String email;
        private String password;
    }
    
    @Data
    public static class AuthResponse {
        private String token;
        private UserDTO.Response user;
    }
}