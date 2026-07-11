package com.example.inventario_service.service;

import com.example.inventario_service.dto.ProveedorDTO;
import com.example.inventario_service.exception.BadRequestException;
import com.example.inventario_service.exception.ExternalServiceException;
import com.example.inventario_service.exception.ResourceNotFoundException;
import com.example.inventario_service.model.MovimientoInventario;
import com.example.inventario_service.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServiceTest {

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MovimientoInventarioService inventarioService;

    private MovimientoInventario entradaValida;
    private ProveedorDTO proveedorActivo;
    private ProveedorDTO proveedorInactivo;

    @BeforeEach
    void setUp() {
        entradaValida = new MovimientoInventario();
        entradaValida.setId(1L);
        entradaValida.setIdProducto(1L);
        entradaValida.setNombreProducto("Torta de Chocolate");
        entradaValida.setTipo("ENTRADA");
        entradaValida.setCantidad(20);
        entradaValida.setStockResultante(20);
        entradaValida.setFecha(LocalDateTime.now());

        proveedorActivo = new ProveedorDTO();
        proveedorActivo.setId(1L);
        proveedorActivo.setNombre("Distribuidora El Trigal");
        proveedorActivo.setActivo(true);

        proveedorInactivo = new ProveedorDTO();
        proveedorInactivo.setId(2L);
        proveedorInactivo.setNombre("Lácteos del Sur");
        proveedorInactivo.setActivo(false);
    }

    private void mockWebClientChain(ProveedorDTO respuesta) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ProveedorDTO.class)).thenReturn(Mono.justOrEmpty(respuesta));
    }

    @Test
    void registrar_conEntrada_noDeberiaConsultarProveedor() {
        // Given
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        MovimientoInventario resultado = inventarioService.registrar(entradaValida);

        // Then
        assertNotNull(resultado);
        assertEquals("ENTRADA", resultado.getTipo());
        verifyNoInteractions(webClientBuilder);
        verify(movimientoRepository, times(1)).save(entradaValida);
    }

    @Test
    void registrar_conSalidaSobreElMinimo_noDeberiaConsultarProveedor() {
        // Given
        MovimientoInventario salida = new MovimientoInventario();
        salida.setIdProducto(1L);
        salida.setNombreProducto("Torta de Chocolate");
        salida.setTipo("SALIDA");
        salida.setCantidad(2);
        salida.setStockResultante(18); // por encima del umbral (10)
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        MovimientoInventario resultado = inventarioService.registrar(salida);

        // Then
        assertNotNull(resultado);
        verifyNoInteractions(webClientBuilder);
        verify(movimientoRepository, times(1)).save(salida);
    }

    @Test
    void registrar_conSalidaBajoElMinimoYProveedorActivo_deberiaGuardarExitosamente() {
        // Given
        MovimientoInventario salida = new MovimientoInventario();
        salida.setIdProducto(3L);
        salida.setNombreProducto("Cupcakes Vainilla (caja 6)");
        salida.setTipo("SALIDA");
        salida.setCantidad(25);
        salida.setStockResultante(5); // bajo el umbral (10)
        salida.setIdProveedor(1L);

        mockWebClientChain(proveedorActivo);
        when(movimientoRepository.save(any(MovimientoInventario.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        MovimientoInventario resultado = inventarioService.registrar(salida);

        // Then
        assertNotNull(resultado);
        verify(movimientoRepository, times(1)).save(salida);
    }

    @Test
    void registrar_conSalidaBajoElMinimoYProveedorInactivo_deberiaLanzarBadRequestException() {
        // Given
        MovimientoInventario salida = new MovimientoInventario();
        salida.setIdProducto(2L);
        salida.setNombreProducto("Kuchen de Manzana");
        salida.setTipo("SALIDA");
        salida.setCantidad(10);
        salida.setStockResultante(5);
        salida.setIdProveedor(2L);

        mockWebClientChain(proveedorInactivo);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> inventarioService.registrar(salida));
        assertTrue(ex.getMessage().contains("no está activo"));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrar_conSalidaBajoElMinimoSinProveedorIndicado_deberiaLanzarBadRequestException() {
        // Given
        MovimientoInventario salida = new MovimientoInventario();
        salida.setIdProducto(2L);
        salida.setNombreProducto("Kuchen de Manzana");
        salida.setTipo("SALIDA");
        salida.setCantidad(10);
        salida.setStockResultante(5);
        // sin idProveedor

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> inventarioService.registrar(salida));
        assertTrue(ex.getMessage().contains("proveedor"));
        verifyNoInteractions(webClientBuilder);
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrar_conFalloDeConexionAProveedorService_deberiaLanzarExternalServiceException() {
        // Given
        MovimientoInventario salida = new MovimientoInventario();
        salida.setIdProducto(3L);
        salida.setNombreProducto("Cupcakes Vainilla (caja 6)");
        salida.setTipo("SALIDA");
        salida.setCantidad(25);
        salida.setStockResultante(5);
        salida.setIdProveedor(1L);

        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection timeout"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> inventarioService.registrar(salida));
        assertTrue(ex.getMessage().contains("proveedor-service"));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void registrar_conTipoInvalido_deberiaLanzarBadRequestException() {
        // Given
        MovimientoInventario invalido = new MovimientoInventario();
        invalido.setIdProducto(1L);
        invalido.setNombreProducto("Producto X");
        invalido.setTipo("DEVOLUCION"); // tipo no soportado
        invalido.setCantidad(1);
        invalido.setStockResultante(10);

        // When / Then
        assertThrows(BadRequestException.class, () -> inventarioService.registrar(invalido));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void listar_conMovimientosExistentes_deberiaRetornarListaCompleta() {
        // Given
        when(movimientoRepository.findAll()).thenReturn(List.of(entradaValida));

        // When
        List<MovimientoInventario> resultado = inventarioService.listar();

        // Then
        assertEquals(1, resultado.size());
        verify(movimientoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarMovimiento() {
        // Given
        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(entradaValida));

        // When
        MovimientoInventario resultado = inventarioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Torta de Chocolate", resultado.getNombreProducto());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> inventarioService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarMovimiento() {
        // Given
        when(movimientoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movimientoRepository).deleteById(1L);

        // When
        inventarioService.eliminar(1L);

        // Then
        verify(movimientoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(movimientoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> inventarioService.eliminar(99L));
        verify(movimientoRepository, never()).deleteById(anyLong());
    }
}
