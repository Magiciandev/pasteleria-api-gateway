package com.example.compra_service.service;

import com.example.compra_service.dto.ProveedorDTO;
import com.example.compra_service.exception.BadRequestException;
import com.example.compra_service.exception.ExternalServiceException;
import com.example.compra_service.exception.ResourceNotFoundException;
import com.example.compra_service.model.Compra;
import com.example.compra_service.model.DetalleCompra;
import com.example.compra_service.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    // Cadena GET (consulta a proveedor-service)
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @Mock
    private WebClient.ResponseSpec getResponseSpec;

    // Cadena POST (carga de stock en inventario-service)
    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private WebClient.RequestBodySpec requestBodySpec;
    @Mock
    private WebClient.ResponseSpec postResponseSpec;

    @InjectMocks
    private CompraService compraService;

    private Compra compraValida;
    private ProveedorDTO proveedorActivo;
    private ProveedorDTO proveedorInactivo;

    @BeforeEach
    void setUp() {
        proveedorActivo = new ProveedorDTO();
        proveedorActivo.setId(1L);
        proveedorActivo.setNombre("Distribuidora El Trigal");
        proveedorActivo.setActivo(true);

        proveedorInactivo = new ProveedorDTO();
        proveedorInactivo.setId(2L);
        proveedorInactivo.setNombre("Lácteos del Sur");
        proveedorInactivo.setActivo(false);

        DetalleCompra detalle1 = new DetalleCompra(1L, "Torta de Chocolate", 10, 2000.0);
        DetalleCompra detalle2 = new DetalleCompra(4L, "Pan de Pascua", 5, 1000.0);

        compraValida = new Compra();
        compraValida.setId(1L);
        compraValida.setIdProveedor(1L);
        compraValida.setNumeroFactura("F-2001");
        List<DetalleCompra> detalles = new ArrayList<>(Arrays.asList(detalle1, detalle2));
        compraValida.setDetalles(detalles);
        compraValida.setEstado("PENDIENTE");
    }

    private void mockWebClientGet(ProveedorDTO respuesta) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(getResponseSpec);
        when(getResponseSpec.bodyToMono(ProveedorDTO.class)).thenReturn(Mono.justOrEmpty(respuesta));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mockWebClientPost() {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        
        // CORRECCIÓN: bodyValue retorna RequestHeadersSpec y le especificamos any(Object.class)
        when(requestBodySpec.bodyValue(any(Object.class))).thenReturn(requestHeadersSpec);
        
        when(requestHeadersSpec.retrieve()).thenReturn(postResponseSpec);
        when(postResponseSpec.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
    }

    @Test
    void guardar_conProveedorActivo_deberiaCalcularMontoTotalYNombreProveedor() {
        // Given
        mockWebClientGet(proveedorActivo);
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Compra resultado = compraService.guardar(compraValida);

        // Then
        assertNotNull(resultado);
        assertEquals("Distribuidora El Trigal", resultado.getNombreProveedor());
        assertEquals(25000.0, resultado.getMontoTotal()); // 10*2000 + 5*1000
        assertEquals("PENDIENTE", resultado.getEstado());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void guardar_conProveedorInactivo_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientGet(proveedorInactivo);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> compraService.guardar(compraValida));
        assertTrue(ex.getMessage().contains("no está activo"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void guardar_conFalloDeConexionAProveedorService_deberiaLanzarExternalServiceException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection timeout"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> compraService.guardar(compraValida));
        assertTrue(ex.getMessage().contains("proveedor-service"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void recibir_conCompraPendiente_deberiaCargarInventarioYCambiarEstado() {
        // Given
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));
        when(webClientBuilder.build()).thenReturn(webClient);
        mockWebClientPost();
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Compra resultado = compraService.recibir(1L);

        // Then
        assertEquals("RECIBIDA", resultado.getEstado());
        verify(requestBodyUriSpec, times(2)).uri(anyString()); // una llamada por cada detalle
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void recibir_conCompraAnulada_deberiaLanzarBadRequestException() {
        // Given
        compraValida.setEstado("ANULADA");
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> compraService.recibir(1L));
        assertTrue(ex.getMessage().contains("ANULADA"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void recibir_conCompraYaRecibida_deberiaLanzarBadRequestException() {
        // Given
        compraValida.setEstado("RECIBIDA");
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> compraService.recibir(1L));
        assertTrue(ex.getMessage().contains("ya fue recibida"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void recibir_conFalloDeConexionAInventarioService_deberiaLanzarExternalServiceException() {
        // Given
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.post()).thenThrow(new RuntimeException("Connection refused"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> compraService.recibir(1L));
        assertTrue(ex.getMessage().contains("inventario-service"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void anular_conCompraPendiente_deberiaAnularExitosamente() {
        // Given
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));
        when(compraRepository.save(any(Compra.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Compra resultado = compraService.anular(1L);

        // Then
        assertEquals("ANULADA", resultado.getEstado());
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void anular_conCompraYaRecibida_deberiaLanzarBadRequestException() {
        // Given
        compraValida.setEstado("RECIBIDA");
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> compraService.anular(1L));
        assertTrue(ex.getMessage().contains("ya fue RECIBIDA"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarCompra() {
        // Given
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compraValida));

        // When
        Compra resultado = compraService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("F-2001", resultado.getNumeroFactura());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> compraService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarCompra() {
        // Given
        when(compraRepository.existsById(1L)).thenReturn(true);
        doNothing().when(compraRepository).deleteById(1L);

        // When
        compraService.eliminar(1L);

        // Then
        verify(compraRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(compraRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class, () -> compraService.eliminar(99L));
        verify(compraRepository, never()).deleteById(anyLong());
    }
}