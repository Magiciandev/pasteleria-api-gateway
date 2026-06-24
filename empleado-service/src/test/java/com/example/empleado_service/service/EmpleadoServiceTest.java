package com.example.empleado_service.service;

import com.example.empleado_service.exception.ResourceNotFoundException;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleadoValido;

    @BeforeEach
    void setUp() {
        empleadoValido = new Empleado();
        empleadoValido.setId(1L);
        empleadoValido.setRutEmpleado("11111111-1");
        empleadoValido.setNombre("Pedro Pastelero");
        empleadoValido.setCargo("Pastelero");
        empleadoValido.setFechaIngreso(LocalDate.of(2020, 1, 1));
        empleadoValido.setSueldoBase(new BigDecimal("1500000.00"));
        empleadoValido.setValorHoraExtra(new BigDecimal("15000.00"));
        empleadoValido.setActivo(true);
    }

    @Test
    void guardar_conDatosValidos_deberiaGuardarEmpleado() {
        // Given
        when(empleadoRepository.save(empleadoValido)).thenReturn(empleadoValido);

        // When
        Empleado resultado = empleadoService.guardar(empleadoValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Pedro Pastelero", resultado.getNombre());
        verify(empleadoRepository, times(1)).save(empleadoValido);
    }

    @Test
    void listar_conEmpleadosExistentes_deberiaRetornarListaCompleta() {
        // Given
        Empleado otroEmpleado = new Empleado();
        otroEmpleado.setId(2L);
        otroEmpleado.setNombre("Ana Repostera");
        when(empleadoRepository.findAll()).thenReturn(Arrays.asList(empleadoValido, otroEmpleado));

        // When
        List<Empleado> resultado = empleadoService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarEmpleado() {
        // Given
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoValido));

        // When
        Empleado resultado = empleadoService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Pedro Pastelero", resultado.getNombre());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> empleadoService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void buscarPorRut_conRutExistente_deberiaRetornarEmpleado() {
        // Given
        when(empleadoRepository.findByRutEmpleado("11111111-1")).thenReturn(Optional.of(empleadoValido));

        // When
        Empleado resultado = empleadoService.buscarPorRut("11111111-1");

        // Then
        assertEquals("11111111-1", resultado.getRutEmpleado());
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(empleadoRepository.findByRutEmpleado("99999999-9")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> empleadoService.buscarPorRut("99999999-9"));
    }

    @Test
    void listarActivos_deberiaRetornarSoloEmpleadosActivos() {
        // Given
        when(empleadoRepository.findByActivo(true)).thenReturn(List.of(empleadoValido));

        // When
        List<Empleado> resultado = empleadoService.listarActivos();

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
    }

    @Test
    void actualizar_conIdExistente_deberiaActualizarDatosDelEmpleado() {
        // Given
        Empleado datosNuevos = new Empleado();
        datosNuevos.setCargo("Jefe de Pasteleria");
        datosNuevos.setSueldoBase(new BigDecimal("1800000.00"));
        datosNuevos.setValorHoraExtra(new BigDecimal("18000.00"));
        datosNuevos.setActivo(true);
        datosNuevos.setTelefono("+56911112222");
        datosNuevos.setDireccion("Nueva direccion");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoValido));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Empleado resultado = empleadoService.actualizar(1L, datosNuevos);

        // Then
        assertEquals("Jefe de Pasteleria", resultado.getCargo());
        assertEquals(new BigDecimal("1800000.00"), resultado.getSueldoBase());
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    void actualizar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> empleadoService.actualizar(99L, empleadoValido));
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarEmpleado() {
        // Given
        when(empleadoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(empleadoRepository).deleteById(1L);

        // When
        empleadoService.eliminar(1L);

        // Then
        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(empleadoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> empleadoService.eliminar(99L));
    }

    @Test
    void calcularAntiguedad_conFechaIngresoDeCuatroAniosAtras_deberiaRetornarAlMenosCuatro() {
        // Given
        Empleado empleado = new Empleado();
        empleado.setFechaIngreso(LocalDate.of(2020, 1, 1));

        // When
        Long antiguedad = empleado.calcularAntigüedad();

        // Then
        assertTrue(antiguedad >= 4);
    }

    @Test
    void calcularAntiguedad_conFechaIngresoHoy_deberiaRetornarCero() {
        // Given
        Empleado empleado = new Empleado();
        empleado.setFechaIngreso(LocalDate.now());

        // When
        Long antiguedad = empleado.calcularAntigüedad();

        // Then
        assertEquals(0L, antiguedad);
    }
}
