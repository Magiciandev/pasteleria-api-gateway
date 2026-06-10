package com.pasteleria.ingrediente.service;

import com.pasteleria.ingrediente.dto.IngredienteDTO;
import com.pasteleria.ingrediente.model.Ingrediente;
import com.pasteleria.ingrediente.repository.IngredienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingrediente;
    private IngredienteDTO ingredienteDTO;

    @BeforeEach
    public void setUp() {
        // Given: Preparar datos de prueba
        ingrediente = new Ingrediente("Harina de trigo", "Harina premium para pastelería", 
                                      50.0, "kg", 2.50);
        ingrediente.setId(1L);

        ingredienteDTO = new IngredienteDTO("Harina de trigo", "Harina premium para pastelería",
                                            50.0, "kg", 2.50);
    }

    @Test
    public void testGuardarIngrediente() {
        // Given: Ingrediente a guardar preparado
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // When: Se guarda el ingrediente
        IngredienteDTO resultado = ingredienteService.guardar(ingredienteDTO);

        // Then: Verificar guardado exitoso
        assertNotNull(resultado);
        assertEquals("Harina de trigo", resultado.getNombre());
        assertEquals(50.0, resultado.getCantidad());
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    public void testListarIngredientes() {
        // Given: Lista de ingredientes
        List<Ingrediente> ingredientes = Arrays.asList(ingrediente);
        when(ingredienteRepository.findAll()).thenReturn(ingredientes);

        // When: Se listan todos
        List<IngredienteDTO> resultado = ingredienteService.listar();

        // Then: Verificar lista correcta
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Harina de trigo", resultado.get(0).getNombre());
        verify(ingredienteRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId() {
        // Given: Ingrediente existente
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));

        // When: Se busca por ID
        Optional<IngredienteDTO> resultado = ingredienteService.buscarPorId(1L);

        // Then: Verificar búsqueda exitosa
        assertTrue(resultado.isPresent());
        assertEquals("Harina de trigo", resultado.get().getNombre());
        verify(ingredienteRepository, times(1)).findById(1L);
    }

    @Test
    public void testBuscarPorIdNoEncontrado() {
        // Given: ID inexistente
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // When: Se busca ID inexistente
        Optional<IngredienteDTO> resultado = ingredienteService.buscarPorId(999L);

        // Then: Verificar que no encuentra nada
        assertFalse(resultado.isPresent());
        verify(ingredienteRepository, times(1)).findById(999L);
    }

    @Test
    public void testBuscarPorNombre() {
        // Given: Ingrediente con nombre específico
        when(ingredienteRepository.findByNombreIgnoreCase("Harina de trigo"))
            .thenReturn(Optional.of(ingrediente));

        // When: Se busca por nombre
        Optional<IngredienteDTO> resultado = ingredienteService.buscarPorNombre("Harina de trigo");

        // Then: Verificar búsqueda
        assertTrue(resultado.isPresent());
        assertEquals(2.50, resultado.get().getPrecioUnitario());
        verify(ingredienteRepository, times(1)).findByNombreIgnoreCase("Harina de trigo");
    }

    @Test
    public void testActualizarIngrediente() {
        // Given: Ingrediente a actualizar
        IngredienteDTO actualizado = new IngredienteDTO("Harina de trigo Premium", 
                                                        "Harina especial de alta calidad", 75.0, "kg", 3.50);
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // When: Se actualiza
        IngredienteDTO resultado = ingredienteService.actualizar(1L, actualizado);

        // Then: Verificar actualización
        assertNotNull(resultado);
        verify(ingredienteRepository, times(1)).findById(1L);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    public void testEliminarIngrediente() {
        // Given: Ingrediente a eliminar
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        // When: Se elimina
        boolean resultado = ingredienteService.eliminar(1L);

        // Then: Verificar eliminación
        assertTrue(resultado);
        verify(ingredienteRepository, times(1)).existsById(1L);
        verify(ingredienteRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testEliminarIngredienteNoExistente() {
        // Given: ID inexistente
        when(ingredienteRepository.existsById(999L)).thenReturn(false);

        // When: Se intenta eliminar
        boolean resultado = ingredienteService.eliminar(999L);

        // Then: Verificar que no se elimina
        assertFalse(resultado);
        verify(ingredienteRepository, times(1)).existsById(999L);
        verify(ingredienteRepository, never()).deleteById(999L);
    }

    @Test
    public void testExistePorId() {
        // Given: ID existente
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        // When: Se verifica existencia
        boolean resultado = ingredienteService.existePorId(1L);

        // Then: Verificar resultado
        assertTrue(resultado);
        verify(ingredienteRepository, times(1)).existsById(1L);
    }

    @Test
    public void testListarActivos() {
        // Given: Ingredientes activos
        List<Ingrediente> activos = Arrays.asList(ingrediente);
        when(ingredienteRepository.findByActivoTrue()).thenReturn(activos);

        // When: Se listan activos
        List<IngredienteDTO> resultado = ingredienteService.listarActivos();

        // Then: Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(ingredienteRepository, times(1)).findByActivoTrue();
    }
}
