package com.eventmanagement.controller;

import com.eventmanagement.dto.UserDTO;
import com.eventmanagement.security.CurrentUser;
import com.eventmanagement.security.UserPrincipal;
import com.eventmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDTO.Response> registerUser(@Valid @RequestBody UserDTO.Request userRequest) {
        return new ResponseEntity<>(userService.registerUser(userRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO.AuthResponse> authenticateUser(@Valid @RequestBody UserDTO.AuthRequest authRequest) {
        return ResponseEntity.ok(userService.authenticateUser(authRequest));
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO.Response> getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(userService.getUserById(currentUser.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO.Response> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO.Response> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserDTO.Request userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }
}