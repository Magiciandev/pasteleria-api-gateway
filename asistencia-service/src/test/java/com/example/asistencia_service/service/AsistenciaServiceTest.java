package com.example.asistencia_service.service;

import com.example.asistencia_service.dto.EmpleadoDTO;
import com.example.asistencia_service.exception.BadRequestException;
import com.example.asistencia_service.exception.ExternalServiceException;
import com.example.asistencia_service.exception.ResourceNotFoundException;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

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
    private AsistenciaService asistenciaService;

    private EmpleadoDTO empleadoValido;

    @BeforeEach
    void setUp() {
        empleadoValido = EmpleadoDTO.builder()
                .id(1L)
                .nombre("Pedro Pastelero")
                .sueldoBase(new BigDecimal("1500000.00"))
                .valorHoraExtra(new BigDecimal("15000.00"))
                .build();
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientChain(EmpleadoDTO respuesta) {
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(EmpleadoDTO.class)).thenReturn(Mono.justOrEmpty(respuesta));
    }

    @Test
    void registrar_conJornadaDeNueveHoras_noDeberiaGenerarHorasExtra() {
        // Given
        mockWebClientChain(empleadoValido);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);
        asistencia.setHoraEntrada(LocalTime.of(8, 0));
        asistencia.setHoraSalida(LocalTime.of(17, 0));
        when(asistenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Asistencia resultado = asistenciaService.registrar(asistencia);

        // Then
        assertEquals(new BigDecimal("9.00"), resultado.getHorasTrabajadas());
        assertEquals(BigDecimal.ZERO, resultado.getHorasExtra());
        assertEquals(BigDecimal.ZERO, resultado.getCostoHorasExtra());
    }

    @Test
    void registrar_conJornadaDeDiezHoras_deberiaCalcularUnaHoraExtra() {
        // Given
        mockWebClientChain(empleadoValido);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);
        asistencia.setHoraEntrada(LocalTime.of(8, 0));
        asistencia.setHoraSalida(LocalTime.of(18, 0));
        when(asistenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Asistencia resultado = asistenciaService.registrar(asistencia);

        // Then
        assertEquals(new BigDecimal("10.00"), resultado.getHorasTrabajadas());
        assertEquals(new BigDecimal("1.00"), resultado.getHorasExtra());
        assertEquals(new BigDecimal("15000.00"), resultado.getCostoHorasExtra());
    }

    @Test
    void registrar_conEmpleadoNoPresente_deberiaDejarHorasEnCero() {
        // Given
        mockWebClientChain(empleadoValido);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(false);
        when(asistenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Asistencia resultado = asistenciaService.registrar(asistencia);

        // Then
        assertEquals(BigDecimal.ZERO, resultado.getHorasTrabajadas());
        assertEquals(BigDecimal.ZERO, resultado.getHorasExtra());
        assertEquals(BigDecimal.ZERO, resultado.getCostoHorasExtra());
    }

    @Test
    void registrar_presenteSinHoraEntrada_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientChain(empleadoValido);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);
        asistencia.setHoraEntrada(null);
        asistencia.setHoraSalida(LocalTime.of(17, 0));

        // When / Then
        assertThrows(BadRequestException.class,
                () -> asistenciaService.registrar(asistencia));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void registrar_conHoraSalidaAntesQueEntrada_deberiaLanzarBadRequestException() {
        // Given
        mockWebClientChain(empleadoValido);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);
        asistencia.setHoraEntrada(LocalTime.of(17, 0));
        asistencia.setHoraSalida(LocalTime.of(8, 0));

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> asistenciaService.registrar(asistencia));
        assertTrue(ex.getMessage().contains("posterior"));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void registrar_conEmpleadoInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        mockWebClientChain(null);
        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(99L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> asistenciaService.registrar(asistencia));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void registrar_conFalloDeConexionAEmpleadoService_deberiaLanzarExternalServiceException() {
        // Given
        when(webClientBuilder.build()).thenReturn(webClient);
        when(webClient.get()).thenThrow(new RuntimeException("Connection refused"));

        Asistencia asistencia = new Asistencia();
        asistencia.setIdEmpleado(1L);
        asistencia.setFecha(LocalDate.now());
        asistencia.setPresente(true);

        // When / Then
        ExternalServiceException ex = assertThrows(ExternalServiceException.class,
                () -> asistenciaService.registrar(asistencia));
        assertTrue(ex.getMessage().contains("empleado-service"));
        verify(asistenciaRepository, never()).save(any());
    }

    @Test
    void listar_conAsistenciasExistentes_deberiaRetornarListaCompleta() {
        // Given
        Asistencia asistencia1 = new Asistencia();
        Asistencia asistencia2 = new Asistencia();
        when(asistenciaRepository.findAll()).thenReturn(List.of(asistencia1, asistencia2));

        // When
        List<Asistencia> resultado = asistenciaService.listar();

        // Then
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarAsistencia() {
        // Given
        Asistencia asistencia = new Asistencia();
        asistencia.setId(1L);
        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(asistencia));

        // When
        Asistencia resultado = asistenciaService.buscarPorId(1L);

        // Then
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(asistenciaRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> asistenciaService.buscarPorId(99L));
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarAsistencia() {
        // Given
        when(asistenciaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(asistenciaRepository).deleteById(1L);

        // When
        asistenciaService.eliminar(1L);

        // Then
        verify(asistenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(asistenciaRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> asistenciaService.eliminar(99L));
    }
}
