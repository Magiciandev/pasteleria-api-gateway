package com.example.envio_service.service;

import com.example.envio_service.dto.ClienteDTO;
import com.example.envio_service.dto.PedidoDTO;
import com.example.envio_service.exception.BadRequestException;
import com.example.envio_service.exception.ExternalServiceException;
import com.example.envio_service.exception.ResourceNotFoundException;
import com.example.envio_service.model.Envio;
import com.example.envio_service.repository.EnvioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock
    private EnvioRepository envioRepository;

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
    private EnvioService envioService;

    private Envio envioValido;
    private PedidoDTO pedidoConfirmado;
    private ClienteDTO clienteValido;

    @BeforeEach
    void setUp() {
        envioValido = new Envio();
        envioValido.setId(1L);
        envioValido.setIdPedido(1L);
        envioValido.setIdCliente(1L);

        pedidoConfirmado = PedidoDTO.builder()
                .id(1L)
                .idCliente(1L)
                .estado("CONFIRMADO")
                .build();

        clienteValido = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan Perez")
                .direccion("Av. Siempre Viva 123")
                .build();
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientGenericChain(Object respuestaPedido, Object respuestaCliente) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(PedidoDTO.class)).thenReturn(Mono.justOrEmpty((PedidoDTO) respuestaPedido));
        if (respuestaCliente != null) {
            when(responseSpec.bodyToMono(ClienteDTO.class)).thenReturn(Mono.justOrEmpty((ClienteDTO) respuestaCliente));
        }
    }

    @Test
    void crearEnvio_conPedidoConfirmadoYClienteExistente_deberiaCrearEnvioExitosamente() {
        // Given
        mockWebClientGenericChain(pedidoConfirmado, clienteValido);
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Envio resultado = envioService.crearEnvio(envioValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombreCliente());
        assertEquals("Av. Siempre Viva 123", resultado.getDireccionEntrega());
        assertEquals("EN_CAMINO", resultado.getEstado());
        verify(envioRepository, times(1)).save(any());
    }

    @Test
    void crearEnvio_sinIdPedidoOIdCliente_deberiaLanzarBadRequestException() {
        // Given
        Envio envioIncompleto = new Envio();
        envioIncompleto.setIdPedido(null);
        envioIncompleto.setIdCliente(1L);

        // When / Then
        assertThrows(BadRequestException.class,
                () -> envioService.crearEnvio(envioIncompleto));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_conPedidoNoConfirmado_deberiaLanzarBadRequestException() {
        // Given
        PedidoDTO pedidoPendiente = PedidoDTO.builder()
                .id(1L)
                .idCliente(1L)
                .estado("PENDIENTE")
                .build();
        mockWebClientGenericChain(pedidoPendiente, null);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> envioService.crearEnvio(envioValido));
        assertTrue(ex.getMessage().contains("CONFIRMADO"));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_conPedidoInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        mockWebClientGenericChain(null, null);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> envioService.crearEnvio(envioValido));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void crearEnvio_conFalloDeConexionAPedidoService_deberiaLanzarExternalServiceException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> envioService.crearEnvio(envioValido));
        assertTrue(ex.getMessage().contains("pedido-service"));
        verify(envioRepository, never()).save(any());
    }

    @Test
    void listar_conEnviosExistentes_deberiaRetornarListaCompleta() {
        // Given
        Envio otroEnvio = new Envio();
        otroEnvio.setId(2L);
        when(envioRepository.findAll()).thenReturn(Arrays.asList(envioValido, otroEnvio));

        // When
        List<Envio> resultado = envioService.listar();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarEnvio() {
        // Given
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioValido));

        // When
        Envio resultado = envioService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(envioRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> envioService.buscarPorId(99L));
    }

    @Test
    void actualizarEstado_aEntregado_deberiaRegistrarFechaEntregaReal() {
        // Given
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioValido));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Envio resultado = envioService.actualizarEstado(1L, "ENTREGADO");

        // Then
        assertEquals("ENTREGADO", resultado.getEstado());
        assertNotNull(resultado.getFechaEntregaReal());
    }

    @Test
    void actualizarEstado_aEnCamino_noDeberiaRegistrarFechaEntregaReal() {
        // Given
        when(envioRepository.findById(1L)).thenReturn(Optional.of(envioValido));
        when(envioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Envio resultado = envioService.actualizarEstado(1L, "EN_CAMINO");

        // Then
        assertEquals("EN_CAMINO", resultado.getEstado());
        assertNull(resultado.getFechaEntregaReal());
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarEnvio() {
        // Given
        when(envioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(envioRepository).deleteById(1L);

        // When
        envioService.eliminar(1L);

        // Then
        verify(envioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(envioRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> envioService.eliminar(99L));
    }
}
