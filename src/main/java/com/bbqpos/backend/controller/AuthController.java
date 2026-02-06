package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.auth.LoginRequest;
import com.bbqpos.backend.dto.auth.LoginResponse;
import com.bbqpos.backend.service.AuthService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/init-admin")
    public ResponseEntity<Void> initAdmin() {
        authService.initAdminUser();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
