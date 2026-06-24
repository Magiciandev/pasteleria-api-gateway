package com.example.cliente_service.service;

import com.example.cliente_service.exception.BadRequestException;
import com.example.cliente_service.exception.ExternalServiceException;
import com.example.cliente_service.exception.ResourceNotFoundException;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.repository.ClienteRepository;
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
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

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
    private ClienteService clienteService;

    private Cliente clienteValido;

    @BeforeEach
    void setUp() {
        clienteValido = new Cliente();
        clienteValido.setId(1L);
        clienteValido.setRutCliente("12345678-9");
        clienteValido.setNombre("Juan Perez");
        clienteValido.setCorreo("juan.perez@mail.com");
        clienteValido.setTelefono("+56912345678");
        clienteValido.setDireccion("Av. Siempre Viva 123");
    }

    private void mockWebClientChain(Boolean respuesta) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class)).thenReturn(Mono.justOrEmpty(respuesta));
    }

    @Test
    void guardar_conUsuarioExistenteEnAuth_deberiaGuardarCliente() {
        // Given
        mockWebClientChain(true);
        when(clienteRepository.save(clienteValido)).thenReturn(clienteValido);

        // When
        Cliente resultado = clienteService.guardar(clienteValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Juan Perez", resultado.getNombre());
        verify(clienteRepository, times(1)).save(clienteValido);
    }

    @Test
    void guardar_conUsuarioInexistenteEnAuth_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientChain(false);

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> clienteService.guardar(clienteValido));
        assertTrue(ex.getMessage().contains("juan.perez@mail.com"));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void guardar_conRespuestaNulaDeAuth_deberiaLanzarExternalServiceException() {
        // Given
        mockWebClientChain(null);

        // When / Then
        assertThrows(ExternalServiceException.class,
                () -> clienteService.guardar(clienteValido));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void guardar_conFalloDeConexionAAuthService_deberiaLanzarExternalServiceException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> clienteService.guardar(clienteValido));
        assertTrue(ex.getMessage().contains("auth-service"));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void listar_conClientesExistentes_deberiaRetornarListaCompleta() {
        // Given
        Cliente otroCliente = new Cliente();
        otroCliente.setId(2L);
        otroCliente.setNombre("Maria Lopez");
        when(clienteRepository.findAll()).thenReturn(Arrays.asList(clienteValido, otroCliente));

        // When
        List<Cliente> resultado = clienteService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarCliente() {
        // Given
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));

        // When
        Cliente resultado = clienteService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> clienteService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void buscarPorRut_conRutExistente_deberiaRetornarCliente() {
        // Given
        when(clienteRepository.findByRutCliente("12345678-9")).thenReturn(Optional.of(clienteValido));

        // When
        Cliente resultado = clienteService.buscarPorRut("12345678-9");

        // Then
        assertEquals("12345678-9", resultado.getRutCliente());
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(clienteRepository.findByRutCliente("00000000-0")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.buscarPorRut("00000000-0"));
    }

    @Test
    void existePorId_conIdExistente_deberiaRetornarTrue() {
        // Given
        when(clienteRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = clienteService.existePorId(1L);

        // Then
        assertTrue(resultado);
    }

    @Test
    void actualizar_conIdExistente_deberiaActualizarDatosDelCliente() {
        // Given
        Cliente datosNuevos = new Cliente();
        datosNuevos.setRutCliente("98765432-1");
        datosNuevos.setNombre("Juan Perez Actualizado");
        datosNuevos.setCorreo("nuevo@mail.com");
        datosNuevos.setTelefono("+56987654321");
        datosNuevos.setDireccion("Nueva Direccion 456");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteValido));
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Cliente resultado = clienteService.actualizar(1L, datosNuevos);

        // Then
        assertEquals("Juan Perez Actualizado", resultado.getNombre());
        assertEquals("nuevo@mail.com", resultado.getCorreo());
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    void actualizar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(clienteRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.actualizar(99L, clienteValido));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarCliente() {
        // Given
        when(clienteRepository.existsById(1L)).thenReturn(true);
        doNothing().when(clienteRepository).deleteById(1L);

        // When
        clienteService.eliminar(1L);

        // Then
        verify(clienteRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(clienteRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.eliminar(99L));
        verify(clienteRepository, never()).deleteById(anyLong());
    }
}
