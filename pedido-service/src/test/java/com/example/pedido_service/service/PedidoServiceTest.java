package com.example.pedido_service.service;

import com.example.pedido_service.dto.ClienteDTO;
import com.example.pedido_service.exception.BadRequestException;
import com.example.pedido_service.exception.ExternalServiceException;
import com.example.pedido_service.exception.ResourceNotFoundException;
import com.example.pedido_service.model.DetallePedido;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

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
    private PedidoService pedidoService;

    private Pedido pedidoValido;
    private ClienteDTO clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new ClienteDTO();
        clienteValido.setId(1L);
        clienteValido.setNombre("Juan Perez");

        DetallePedido detalle1 = new DetallePedido(1L, "Torta Tres Leches", 2, 15000.0);
        DetallePedido detalle2 = new DetallePedido(2L, "Torta de Chocolate", 1, 18000.0);

        pedidoValido = new Pedido();
        pedidoValido.setId(1L);
        pedidoValido.setIdCliente(1L);
        List<DetallePedido> detalles = new ArrayList<>(Arrays.asList(detalle1, detalle2));
        pedidoValido.setDetalles(detalles);
        pedidoValido.setEstado("PENDIENTE");
    }

    private void mockWebClientChain(ClienteDTO respuesta) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.justOrEmpty(respuesta));
    }

    @Test
    void guardar_conClienteExistenteYDetalles_deberiaCalcularTotalAutomaticamente() {
        // Given
        mockWebClientChain(clienteValido);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pedido resultado = pedidoService.guardar(pedidoValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertEquals(48000.0, resultado.getTotal());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void guardar_conClienteInexistente_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientChain(null);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.guardar(pedidoValido));
        assertTrue(ex.getMessage().contains("no existe"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void guardar_conFalloDeConexionAClienteService_deberiaLanzarExternalServiceException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection timeout"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> pedidoService.guardar(pedidoValido));
        assertTrue(ex.getMessage().contains("cliente-service"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void guardar_sinDetalles_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientChain(clienteValido);
        Pedido pedidoSinDetalles = new Pedido();
        pedidoSinDetalles.setIdCliente(1L);
        pedidoSinDetalles.setDetalles(new ArrayList<>());

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> pedidoService.guardar(pedidoSinDetalles));
        assertTrue(ex.getMessage().contains("al menos un detalle"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void listar_conPedidosExistentes_deberiaRetornarListaCompleta() {
        // Given
        Pedido otroPedido = new Pedido();
        otroPedido.setId(2L);
        when(pedidoRepository.findAll()).thenReturn(Arrays.asList(pedidoValido, otroPedido));

        // When
        List<Pedido> resultado = pedidoService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarPedido() {
        // Given
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoValido));

        // When
        Pedido resultado = pedidoService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.getDetalles().size());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.buscarPorId(99L));
    }

    @Test
    void buscarPorIdCliente_deberiaRetornarPedidosDelCliente() {
        // Given
        when(pedidoRepository.findByIdCliente(1L)).thenReturn(List.of(pedidoValido));

        // When
        List<Pedido> resultado = pedidoService.buscarPorIdCliente(1L);

        // Then
        assertEquals(1, resultado.size());
        verify(pedidoRepository, times(1)).findByIdCliente(1L);
    }

    @Test
    void actualizar_conNuevoEstado_deberiaActualizarSoloEstado() {
        // Given
        Pedido cambios = new Pedido();
        cambios.setEstado("CONFIRMADO");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoValido));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Pedido resultado = pedidoService.actualizar(1L, cambios);

        // Then
        assertEquals("CONFIRMADO", resultado.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void actualizar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.actualizar(99L, pedidoValido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarPedido() {
        // Given
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        // When
        pedidoService.eliminar(1L);

        // Then
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> pedidoService.eliminar(99L));
        verify(pedidoRepository, never()).deleteById(anyLong());
    }
}
