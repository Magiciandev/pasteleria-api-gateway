package com.example.auth.service;

import com.example.auth.exception.BadRequestException;
import com.example.auth.exception.ResourceNotFoundException;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HashService hashService;

    @InjectMocks
    private UserService userService;

    private User usuarioValido;

    @BeforeEach
    void setUp() {
        usuarioValido = new User();
        usuarioValido.setId(1L);
        usuarioValido.setEmail("juan@mail.com");
        usuarioValido.setPassword("hash_correcto");
        usuarioValido.setRole("USER");
    }

    @Test
    void login_conCredencialesCorrectas_deberiaRetornarToken() {
        // Given
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(usuarioValido);
        when(hashService.sha1("clave123")).thenReturn("hash_correcto");
        when(jwtService.generateToken("juan@mail.com")).thenReturn("token.jwt.generado");

        // When
        String resultado = userService.login("juan@mail.com", "clave123");

        // Then
        assertEquals("token.jwt.generado", resultado);
        verify(jwtService, times(1)).generateToken("juan@mail.com");
    }

    @Test
    void login_conUsuarioInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(null);

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> userService.login("noexiste@mail.com", "clave123"));
        assertTrue(ex.getMessage().contains("noexiste@mail.com"));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void login_conPasswordIncorrecta_deberiaLanzarBadRequestException() {
        // Given
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(usuarioValido);
        when(hashService.sha1("claveIncorrecta")).thenReturn("hash_incorrecto");

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.login("juan@mail.com", "claveIncorrecta"));
        assertEquals("Credenciales incorrectas", ex.getMessage());
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    void register_conEmailNuevo_deberiaCrearUsuarioExitosamente() {
        // Given
        when(userRepository.findByEmail("nuevo@mail.com")).thenReturn(null);
        when(hashService.sha1("clave123")).thenReturn("hash_generado");

        // When
        String resultado = userService.register("nuevo@mail.com", "clave123");

        // Then
        assertEquals("Usuario creado exitosamente!", resultado);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_conEmailExistente_deberiaLanzarBadRequestException() {
        // Given
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(usuarioValido);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> userService.register("juan@mail.com", "claveNueva"));
        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void existePorEmail_conEmailExistente_deberiaRetornarTrue() {
        // Given
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(usuarioValido);

        // When
        boolean resultado = userService.existePorEmail("juan@mail.com");

        // Then
        assertTrue(resultado);
    }

    @Test
    void existePorEmail_conEmailInexistente_deberiaRetornarFalse() {
        // Given
        when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(null);

        // When
        boolean resultado = userService.existePorEmail("noexiste@mail.com");

        // Then
        assertFalse(resultado);
    }

    @Test
    void getRole_conUsuarioExistente_deberiaRetornarRol() {
        // Given
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(usuarioValido);

        // When
        String resultado = userService.getRole("juan@mail.com");

        // Then
        assertEquals("USER", resultado);
    }

    @Test
    void getRole_conUsuarioInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(userRepository.findByEmail("noexiste@mail.com")).thenReturn(null);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getRole("noexiste@mail.com"));
    }
}
