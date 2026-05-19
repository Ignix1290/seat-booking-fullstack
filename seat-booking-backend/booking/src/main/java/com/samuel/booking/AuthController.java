package com.samuel.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;
    private final BookingServices bookingServices;

    public AuthController(JwtService jwtService, BookingServices bookingServices) {
        this.jwtService = jwtService;
        this.bookingServices = bookingServices;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        User newUser = new User();
        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());

        User savedUser = bookingServices.addUserData(newUser);
        String token = jwtService.generateToken(savedUser.getName());

        return ResponseEntity.ok(new AuthResponse(token, savedUser.getId()));
    }
}