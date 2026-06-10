package com.pasteleria.proveedor.service;

import com.pasteleria.proveedor.dto.ProveedorDTO;
import com.pasteleria.proveedor.model.Proveedor;
import com.pasteleria.proveedor.repository.ProveedorRepository;
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
public class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor;
    private ProveedorDTO proveedorDTO;

    @BeforeEach
    public void setUp() {
        // Given: Preparar datos de prueba
        proveedor = new Proveedor("Azúcares Premium", "Juan García", "juan@azucares.com", 
                                  "+34912345678", "Calle Principal 123", "Madrid", "España");
        proveedor.setId(1L);

        proveedorDTO = new ProveedorDTO("Azúcares Premium", "Juan García", "juan@azucares.com",
                                        "+34912345678", "Calle Principal 123", "Madrid", "España");
    }

    @Test
    public void testGuardarProveedor() {
        // Given: Proveedor a guardar preparado en setUp
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        // When: Se guarda el proveedor
        ProveedorDTO resultado = proveedorService.guardar(proveedorDTO);

        // Then: Verificar que se guardó correctamente
        assertNotNull(resultado);
        assertEquals("Azúcares Premium", resultado.getNombre());
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    public void testListarProveedores() {
        // Given: Lista de proveedores preparada
        List<Proveedor> proveedores = Arrays.asList(proveedor);
        when(proveedorRepository.findAll()).thenReturn(proveedores);

        // When: Se listan todos los proveedores
        List<ProveedorDTO> resultado = proveedorService.listar();

        // Then: Verificar que se retorna la lista correctamente
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Azúcares Premium", resultado.get(0).getNombre());
        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId() {
        // Given: Proveedor existente
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));

        // When: Se busca por ID
        Optional<ProveedorDTO> resultado = proveedorService.buscarPorId(1L);

        // Then: Verificar que encuentra el proveedor
        assertTrue(resultado.isPresent());
        assertEquals("Azúcares Premium", resultado.get().getNombre());
        verify(proveedorRepository, times(1)).findById(1L);
    }

    @Test
    public void testBuscarPorIdNoEncontrado() {
        // Given: ID que no existe
        when(proveedorRepository.findById(999L)).thenReturn(Optional.empty());

        // When: Se busca por ID inexistente
        Optional<ProveedorDTO> resultado = proveedorService.buscarPorId(999L);

        // Then: Verificar que no encuentra nada
        assertFalse(resultado.isPresent());
        verify(proveedorRepository, times(1)).findById(999L);
    }

    @Test
    public void testBuscarPorNombre() {
        // Given: Proveedor con nombre específico
        when(proveedorRepository.findByNombreIgnoreCase("Azúcares Premium"))
            .thenReturn(Optional.of(proveedor));

        // When: Se busca por nombre
        Optional<ProveedorDTO> resultado = proveedorService.buscarPorNombre("Azúcares Premium");

        // Then: Verificar búsqueda exitosa
        assertTrue(resultado.isPresent());
        assertEquals("juan@azucares.com", resultado.get().getEmail());
        verify(proveedorRepository, times(1)).findByNombreIgnoreCase("Azúcares Premium");
    }

    @Test
    public void testActualizarProveedor() {
        // Given: Proveedor existente y datos actualizados
        ProveedorDTO actualizado = new ProveedorDTO("Azúcares Premium Updated", "Carlos López", 
                                                     "carlos@azucares.com", "+34987654321", 
                                                     "Calle Nueva 456", "Barcelona", "España");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor));
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor);

        // When: Se actualiza el proveedor
        ProveedorDTO resultado = proveedorService.actualizar(1L, actualizado);

        // Then: Verificar que se actualiza correctamente
        assertNotNull(resultado);
        verify(proveedorRepository, times(1)).findById(1L);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    public void testEliminarProveedor() {
        // Given: Proveedor a eliminar
        when(proveedorRepository.existsById(1L)).thenReturn(true);

        // When: Se elimina el proveedor
        boolean resultado = proveedorService.eliminar(1L);

        // Then: Verificar eliminación exitosa
        assertTrue(resultado);
        verify(proveedorRepository, times(1)).existsById(1L);
        verify(proveedorRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testEliminarProveedorNoExistente() {
        // Given: ID que no existe
        when(proveedorRepository.existsById(999L)).thenReturn(false);

        // When: Se intenta eliminar proveedor inexistente
        boolean resultado = proveedorService.eliminar(999L);

        // Then: Verificar que no se elimina
        assertFalse(resultado);
        verify(proveedorRepository, times(1)).existsById(999L);
        verify(proveedorRepository, never()).deleteById(999L);
    }

    @Test
    public void testExistePorId() {
        // Given: ID existente
        when(proveedorRepository.existsById(1L)).thenReturn(true);

        // When: Se verifica existencia
        boolean resultado = proveedorService.existePorId(1L);

        // Then: Verificar resultado
        assertTrue(resultado);
        verify(proveedorRepository, times(1)).existsById(1L);
    }

    @Test
    public void testListarActivos() {
        // Given: Lista de proveedores activos
        List<Proveedor> activos = Arrays.asList(proveedor);
        when(proveedorRepository.findByActivoTrue()).thenReturn(activos);

        // When: Se listan proveedores activos
        List<ProveedorDTO> resultado = proveedorService.listarActivos();

        // Then: Verificar que se retorna lista correcta
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActivo());
        verify(proveedorRepository, times(1)).findByActivoTrue();
    }
}
