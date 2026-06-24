package com.example.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.auth.dto.AuthRequestDTO;
import com.example.auth.service.UserService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody AuthRequestDTO request) {
        logger.info("POST /auth/login - email={}", request.getEmail());
        String token = userService.login(request.getEmail(), request.getPassword());
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("token", token);
        logger.info("Login exitoso email={}", request.getEmail());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody AuthRequestDTO request) {
        logger.info("POST /auth/register - email={}", request.getEmail());
        String resultado = userService.register(request.getEmail(), request.getPassword());
        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/user/{email}/exists")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {
        return ResponseEntity.ok(userService.existePorEmail(email));
    }
}
