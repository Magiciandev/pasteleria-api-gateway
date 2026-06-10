package com.pasteleria.proveedor.controller;

import com.pasteleria.proveedor.dto.ProveedorDTO;
import com.pasteleria.proveedor.service.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProveedorControllerTest {

    @Mock
    private ProveedorService proveedorService;

    @InjectMocks
    private ProveedorController proveedorController;

    private ProveedorDTO proveedorDTO;

    @BeforeEach
    public void setUp() {
        // Given: Preparar datos de prueba
        proveedorDTO = new ProveedorDTO(1L, "Azúcares Premium", "Juan García", "juan@azucares.com",
                                        "+34912345678", "Calle Principal 123", "Madrid", "España", true);
    }

    @Test
    public void testCrearProveedor() {
        // Given: Proveedor a crear
        when(proveedorService.guardar(any(ProveedorDTO.class))).thenReturn(proveedorDTO);

        // When & Then: Verificar creación
        ResponseEntity<ProveedorDTO> resultado = proveedorController.crear(proveedorDTO);

        // Then: Verificar respuesta
        assert resultado.getStatusCode() == HttpStatus.CREATED;
    }

    @Test
    public void testListarProveedores() {
        // Given: Lista de proveedores
        List<ProveedorDTO> proveedores = List.of(proveedorDTO);
        when(proveedorService.listar()).thenReturn(proveedores);

        // When: Se listan
        ResponseEntity<List<ProveedorDTO>> resultado = proveedorController.listar();

        // Then: Verificar resultado
        assert resultado.getStatusCode() == HttpStatus.OK;
        assert resultado.getBody().size() == 1;
    }

    @Test
    public void testBuscarPorId() {
        // Given: Proveedor existente
        when(proveedorService.buscarPorId(1L)).thenReturn(Optional.of(proveedorDTO));

        // When: Se busca
        ResponseEntity<ProveedorDTO> resultado = proveedorController.buscarPorId(1L);

        // Then: Verificar resultado
        assert resultado.getStatusCode() == HttpStatus.OK;
        assert resultado.getBody().getNombre().equals("Azúcares Premium");
    }
}
