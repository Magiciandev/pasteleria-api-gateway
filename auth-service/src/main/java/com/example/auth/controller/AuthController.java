package com.example.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.auth.service.UserService;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("POST /auth/login - email={}", email);
        String token = userService.login(email, request.get("password"));
        Map<String, String> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("token", token);
        logger.info("Login exitoso email={}", email);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        logger.info("POST /auth/register - email={}", email);
        String resultado = userService.register(email, request.get("password"));
        Map<String, String> resp = new HashMap<>();
        resp.put("message", resultado);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/user/{email}/exists")
    public ResponseEntity<Boolean> existeUsuario(@PathVariable String email) {
        return ResponseEntity.ok(userService.existePorEmail(email));
    }
}
