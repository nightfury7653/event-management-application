package com.eventmanagement.controller;

import com.eventmanagement.dto.RegistrationDTO;
import com.eventmanagement.model.Registration;
import com.eventmanagement.security.CurrentUser;
import com.eventmanagement.security.UserPrincipal;
import com.eventmanagement.service.RegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/registrations")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RegistrationDTO.Response> registerForEvent(
            @Valid @RequestBody RegistrationDTO.Request request,
            @CurrentUser UserPrincipal currentUser) {
        return new ResponseEntity<>(
                registrationService.registerForEvent(request, currentUser.getId()),
                HttpStatus.CREATED);
    }

    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RegistrationDTO.Response>> getUserRegistrations(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(registrationService.getUserRegistrations(currentUser.getId()));
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<List<RegistrationDTO.Response>> getEventRegistrations(@PathVariable String eventId) {
        return ResponseEntity.ok(registrationService.getEventRegistrations(eventId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    public ResponseEntity<RegistrationDTO.Response> updateRegistrationStatus(
            @PathVariable String id,
            @RequestParam Registration.RegistrationStatus status) {
        return ResponseEntity.ok(registrationService.updateRegistrationStatus(id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable String id,
            @CurrentUser UserPrincipal currentUser) {
        registrationService.cancelRegistration(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}