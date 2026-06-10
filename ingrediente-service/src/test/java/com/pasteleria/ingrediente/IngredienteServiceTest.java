package com.pasteleria.ingrediente.service;

import com.pasteleria.ingrediente.model.Ingrediente;
import com.pasteleria.ingrediente.dto.IngredienteDTO;
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

    private IngredienteDTO ingredienteDTO;
    private Ingrediente ingrediente;

    @BeforeEach
    public void setUp() {
        // Given: Preparar datos de prueba
        ingredienteDTO = new IngredienteDTO(null, "Harina", "Harina blanca 0000", 100.0, "kg", 25.0, true);
        ingrediente = new Ingrediente("Harina", "Harina blanca 0000", 100.0, "kg", 25.0);
        ingrediente.setId(1L);
    }

    @Test
    public void testGuardarIngrediente() {
        // Given: Ingrediente DTO preparado en setUp
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // When: Se guarda el ingrediente
        IngredienteDTO resultado = ingredienteService.guardar(ingredienteDTO);

        // Then: Verificar resultado
        assertNotNull(resultado);
        assertEquals("Harina", resultado.getNombre());
        assertEquals(100.0, resultado.getCantidad());
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    public void testListarIngredientes() {
        // Given: Lista de ingredientes en el repositorio
        List<Ingrediente> ingredientes = Arrays.asList(ingrediente);
        when(ingredienteRepository.findAll()).thenReturn(ingredientes);

        // When: Se listan los ingredientes
        List<IngredienteDTO> resultado = ingredienteService.listar();

        // Then: Verificar resultado
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Harina", resultado.get(0).getNombre());
        verify(ingredienteRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId() {
        // Given: Ingrediente en el repositorio
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));

        // When: Se busca el ingrediente
        Optional<IngredienteDTO> resultado = ingredienteService.buscarPorId(1L);

        // Then: Verificar resultado
        assertTrue(resultado.isPresent());
        assertEquals("Harina", resultado.get().getNombre());
        verify(ingredienteRepository, times(1)).findById(1L);
    }

    @Test
    public void testBuscarPorIdNoEncontrado() {
        // Given: Ingrediente no existe
        when(ingredienteRepository.findById(999L)).thenReturn(Optional.empty());

        // When: Se busca el ingrediente
        Optional<IngredienteDTO> resultado = ingredienteService.buscarPorId(999L);

        // Then: Verificar resultado
        assertFalse(resultado.isPresent());
        verify(ingredienteRepository, times(1)).findById(999L);
    }

    @Test
    public void testActualizarIngrediente() {
        // Given: Ingrediente existente
        IngredienteDTO actualizadoDTO = new IngredienteDTO(1L, "Harina Premium", "Harina blanca especial", 150.0, "kg", 30.0, true);
        when(ingredienteRepository.findById(1L)).thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente);

        // When: Se actualiza el ingrediente
        IngredienteDTO resultado = ingredienteService.actualizar(1L, actualizadoDTO);

        // Then: Verificar resultado
        assertNotNull(resultado);
        verify(ingredienteRepository, times(1)).findById(1L);
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    public void testEliminarIngrediente() {
        // Given: Ingrediente existe
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        // When: Se elimina el ingrediente
        boolean resultado = ingredienteService.eliminar(1L);

        // Then: Verificar resultado
        assertTrue(resultado);
        verify(ingredienteRepository, times(1)).existsById(1L);
        verify(ingredienteRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testEliminarIngredienteNoExistente() {
        // Given: Ingrediente no existe
        when(ingredienteRepository.existsById(999L)).thenReturn(false);

        // When: Se intenta eliminar
        boolean resultado = ingredienteService.eliminar(999L);

        // Then: Verificar resultado
        assertFalse(resultado);
        verify(ingredienteRepository, times(1)).existsById(999L);
        verify(ingredienteRepository, never()).deleteById(999L);
    }

    @Test
    public void testExistePorId() {
        // Given: Ingrediente existe
        when(ingredienteRepository.existsById(1L)).thenReturn(true);

        // When: Se verifica existencia
        boolean resultado = ingredienteService.existePorId(1L);

        // Then: Verificar resultado
        assertTrue(resultado);
        verify(ingredienteRepository, times(1)).existsById(1L);
    }
}
