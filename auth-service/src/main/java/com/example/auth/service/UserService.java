package com.example.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.auth.exception.BadRequestException;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HashService hashService;

    public String login(String email, String password) {
        logger.info("Intento de login: email={}", email);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.warn("Login fallido - usuario no encontrado: email={}", email);
            throw new ResourceNotFoundException("Usuario no encontrado: " + email);
        }
        if (!hashService.sha1(password).equals(user.getPassword())) {
            logger.warn("Login fallido - credenciales incorrectas: email={}", email);
            throw new BadRequestException("Credenciales incorrectas");
        }
        String token = jwtService.generateToken(email);
        logger.info("Login exitoso: email={}", email);
        return token;
    }

    public String getRole(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new ResourceNotFoundException("Usuario no encontrado: " + email);
        return user.getRole();
    }

    public String register(String email, String password) {
        logger.info("Registrando usuario: email={}", email);
        if (userRepository.findByEmail(email) != null) {
            logger.warn("Registro fallido - usuario ya existe: email={}", email);
            throw new BadRequestException("Ya existe un usuario con el email: " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashService.sha1(password));
        user.setRole("USER");
        userRepository.save(user);
        logger.info("Usuario registrado exitosamente: email={}", email);
        return "Usuario creado exitosamente!";
    }

    public boolean existePorEmail(String email) {
        return userRepository.findByEmail(email) != null;
    }
}
