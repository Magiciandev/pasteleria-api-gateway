package com.pasteleria.inventario.service;

import com.pasteleria.inventario.dto.InventarioItemDTO;
import com.pasteleria.inventario.model.InventarioItem;
import com.pasteleria.inventario.repository.InventarioRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private InventarioService inventarioService;

    private InventarioItem item;
    private InventarioItemDTO itemDTO;

    @BeforeEach
    public void setUp() {
        // Given: Preparar datos de prueba
        item = new InventarioItem(1L, 1L, 100.0, 10.0, 500.0, 5.50, "kg");
        item.setId(1L);

        itemDTO = new InventarioItemDTO(1L, 1L, 100.0, 10.0, 500.0, 5.50, "kg");
    }

    @Test
    public void testGuardarInventarioItem() {
        // Given: Item a guardar con validaciones mockeadas
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class))
            .thenReturn(Mono.just(true));
        when(inventarioRepository.save(any(InventarioItem.class))).thenReturn(item);

        // When: Se guarda el item
        InventarioItemDTO resultado = inventarioService.guardar(itemDTO);

        // Then: Verificar guardado correcto
        assertNotNull(resultado);
        assertEquals(1L, resultado.getIngredienteId());
        assertEquals(100.0, resultado.getStockActual());
        verify(inventarioRepository, times(1)).save(any(InventarioItem.class));
    }

    @Test
    public void testGuardarInventarioItemIngredienteNoExiste() {
        // Given: Ingrediente que no existe
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), any(Object[].class)))
            .thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Boolean.class))
            .thenReturn(Mono.just(false));

        // When: Se intenta guardar con ingrediente no existente
        // Then: Debe lanzar excepción
        assertThrows(RuntimeException.class, () -> {
            inventarioService.guardar(itemDTO);
        });
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    public void testListarInventario() {
        // Given: Lista de items preparada
        List<InventarioItem> items = Arrays.asList(item);
        when(inventarioRepository.findAll()).thenReturn(items);

        // When: Se listan todos los items
        List<InventarioItemDTO> resultado = inventarioService.listar();

        // Then: Verificar lista correcta
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorId() {
        // Given: Item existente
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(item));

        // When: Se busca por ID
        Optional<InventarioItemDTO> resultado = inventarioService.buscarPorId(1L);

        // Then: Verificar búsqueda exitosa
        assertTrue(resultado.isPresent());
        assertEquals(100.0, resultado.get().getStockActual());
        verify(inventarioRepository, times(1)).findById(1L);
    }

    @Test
    public void testBuscarPorIngrediente() {
        // Given: Items de un ingrediente
        List<InventarioItem> items = Arrays.asList(item);
        when(inventarioRepository.findByIngredienteId(1L)).thenReturn(items);

        // When: Se busca por ingrediente
        List<InventarioItemDTO> resultado = inventarioService.buscarPorIngrediente(1L);

        // Then: Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findByIngredienteId(1L);
    }

    @Test
    public void testBuscarPorProveedor() {
        // Given: Items de un proveedor
        List<InventarioItem> items = Arrays.asList(item);
        when(inventarioRepository.findByProveedorId(1L)).thenReturn(items);

        // When: Se busca por proveedor
        List<InventarioItemDTO> resultado = inventarioService.buscarPorProveedor(1L);

        // Then: Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findByProveedorId(1L);
    }

    @Test
    public void testActualizarStock() {
        // Given: Item a actualizar
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventarioRepository.save(any(InventarioItem.class))).thenReturn(item);

        // When: Se actualiza el stock
        InventarioItemDTO resultado = inventarioService.actualizarStock(1L, 250.0);

        // Then: Verificar actualización
        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).findById(1L);
        verify(inventarioRepository, times(1)).save(any(InventarioItem.class));
    }

    @Test
    public void testObtenerStockBajo() {
        // Given: Items con stock bajo
        InventarioItem itemBajo = new InventarioItem(1L, 1L, 5.0, 10.0, 500.0, 5.50, "kg");
        List<InventarioItem> items = Arrays.asList(itemBajo);
        when(inventarioRepository.findByStockActualLessThanEqual(0.0)).thenReturn(items);

        // When: Se obtienen items con stock bajo
        List<InventarioItemDTO> resultado = inventarioService.obtenerStockBajo();

        // Then: Verificar resultados
        assertNotNull(resultado);
        verify(inventarioRepository, times(1)).findByStockActualLessThanEqual(0.0);
    }

    @Test
    public void testEliminarInventario() {
        // Given: Item a eliminar
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        // When: Se elimina
        boolean resultado = inventarioService.eliminar(1L);

        // Then: Verificar eliminación
        assertTrue(resultado);
        verify(inventarioRepository, times(1)).existsById(1L);
        verify(inventarioRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testExistePorId() {
        // Given: ID existente
        when(inventarioRepository.existsById(1L)).thenReturn(true);

        // When: Se verifica existencia
        boolean resultado = inventarioService.existePorId(1L);

        // Then: Verificar resultado
        assertTrue(resultado);
        verify(inventarioRepository, times(1)).existsById(1L);
    }

    @Test
    public void testListarActivos() {
        // Given: Items activos
        List<InventarioItem> items = Arrays.asList(item);
        when(inventarioRepository.findByActivoTrue()).thenReturn(items);

        // When: Se listan activos
        List<InventarioItemDTO> resultado = inventarioService.listarActivos();

        // Then: Verificar resultados
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(inventarioRepository, times(1)).findByActivoTrue();
    }
}
