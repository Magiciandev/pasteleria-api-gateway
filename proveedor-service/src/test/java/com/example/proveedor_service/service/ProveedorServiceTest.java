package com.example.proveedor_service.service;

import com.example.proveedor_service.exception.BadRequestException;
import com.example.proveedor_service.exception.ResourceNotFoundException;
import com.example.proveedor_service.model.Proveedor;
import com.example.proveedor_service.repository.ProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedorValido;

    @BeforeEach
    void setUp() {
        proveedorValido = new Proveedor();
        proveedorValido.setId(1L);
        proveedorValido.setNombre("Distribuidora El Trigal");
        proveedorValido.setRut("76543210-9");
        proveedorValido.setCorreo("contacto@eltrigal.cl");
        proveedorValido.setTelefono("+56912345678");
        proveedorValido.setDireccion("Av. Libertador 456, Santiago");
        proveedorValido.setActivo(true);
        proveedorValido.setFechaRegistro(LocalDate.of(2024, 1, 15));
    }

    @Test
    void guardar_conRutNuevo_deberiaGuardarExitosamente() {
        // Given
        when(proveedorRepository.findByRut(proveedorValido.getRut())).thenReturn(Optional.empty());
        when(proveedorRepository.save(proveedorValido)).thenReturn(proveedorValido);

        // When
        Proveedor resultado = proveedorService.guardar(proveedorValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Distribuidora El Trigal", resultado.getNombre());
        verify(proveedorRepository, times(1)).save(proveedorValido);
    }

    @Test
    void guardar_conRutDuplicado_deberiaLanzarBadRequestException() {
        // Given
        when(proveedorRepository.findByRut(proveedorValido.getRut())).thenReturn(Optional.of(proveedorValido));

        // When / Then
        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> proveedorService.guardar(proveedorValido));
        assertTrue(ex.getMessage().contains(proveedorValido.getRut()));
        verify(proveedorRepository, never()).save(any());
    }

    @Test
    void listar_conProveedoresExistentes_deberiaRetornarListaCompleta() {
        // Given
        Proveedor otroProveedor = new Proveedor();
        otroProveedor.setId(2L);
        otroProveedor.setNombre("Lácteos del Sur");
        when(proveedorRepository.findAll()).thenReturn(Arrays.asList(proveedorValido, otroProveedor));

        // When
        List<Proveedor> resultado = proveedorService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    void listarActivos_deberiaRetornarSoloProveedoresActivos() {
        // Given
        when(proveedorRepository.findByActivo(true)).thenReturn(List.of(proveedorValido));

        // When
        List<Proveedor> resultado = proveedorService.listarActivos();

        // Then
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(proveedorRepository, times(1)).findByActivo(true);
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarProveedor() {
        // Given
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorValido));

        // When
        Proveedor resultado = proveedorService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Distribuidora El Trigal", resultado.getNombre());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> proveedorService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void buscarPorRut_conRutExistente_deberiaRetornarProveedor() {
        // Given
        when(proveedorRepository.findByRut("76543210-9")).thenReturn(Optional.of(proveedorValido));

        // When
        Proveedor resultado = proveedorService.buscarPorRut("76543210-9");

        // Then
        assertNotNull(resultado);
        assertEquals("76543210-9", resultado.getRut());
    }

    @Test
    void buscarPorRut_conRutInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(proveedorRepository.findByRut("00000000-0")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> proveedorService.buscarPorRut("00000000-0"));
    }

    @Test
    void existePorId_conIdExistente_deberiaRetornarTrue() {
        // Given
        when(proveedorRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = proveedorService.existePorId(1L);

        // Then
        assertTrue(resultado);
    }

    @Test
    void actualizar_conIdExistente_deberiaActualizarProveedor() {
        // Given
        Proveedor datosNuevos = new Proveedor();
        datosNuevos.setNombre("Distribuidora El Trigal Actualizada");
        datosNuevos.setRut("76543210-9");
        datosNuevos.setCorreo("nuevo@eltrigal.cl");
        datosNuevos.setTelefono("+56987654321");
        datosNuevos.setDireccion("Nueva Dirección 789");
        datosNuevos.setActivo(true);
        datosNuevos.setFechaRegistro(LocalDate.of(2024, 1, 15));

        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorValido));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Proveedor resultado = proveedorService.actualizar(1L, datosNuevos);

        // Then
        assertEquals("Distribuidora El Trigal Actualizada", resultado.getNombre());
        assertEquals("nuevo@eltrigal.cl", resultado.getCorreo());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void actualizar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> proveedorService.actualizar(99L, proveedorValido));
        verify(proveedorRepository, never()).save(any());
    }

    @Test
    void cambiarEstado_conIdExistente_deberiaActualizarEstado() {
        // Given
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedorValido));
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(inv -> inv.getArgument(0));

        // When
        Proveedor resultado = proveedorService.cambiarEstado(1L, false);

        // Then
        assertFalse(resultado.getActivo());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarProveedor() {
        // Given
        when(proveedorRepository.existsById(1L)).thenReturn(true);
        doNothing().when(proveedorRepository).deleteById(1L);

        // When
        proveedorService.eliminar(1L);

        // Then
        verify(proveedorRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(proveedorRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> proveedorService.eliminar(99L));
        verify(proveedorRepository, never()).deleteById(anyLong());
    }
}
