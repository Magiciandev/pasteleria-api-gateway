package com.example.producto_service.service;

import com.example.producto_service.exception.ResourceNotFoundException;
import com.example.producto_service.model.DetalleProducto;
import com.example.producto_service.model.Producto;
import com.example.producto_service.repository.DetalleProductoRepository;
import com.example.producto_service.repository.ProductoRepository;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DetalleProductoRepository detalleProductoRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto productoValido;
    private DetalleProducto detalleValido;

    @BeforeEach
    void setUp() {
        detalleValido = new DetalleProducto();
        detalleValido.setId(1L);
        detalleValido.setDescripcion("Torta de chocolate artesanal");
        detalleValido.setContieneLactosa(true);
        detalleValido.setContieneHuevos(true);
        detalleValido.setContieneGluten(true);

        productoValido = new Producto();
        productoValido.setId(1L);
        productoValido.setNombre("Torta de Chocolate");
        productoValido.setPrecio(18000.0);
        productoValido.setStock(8);
        productoValido.setDetalleProducto(detalleValido);
    }

    @Test
    void guardar_conDetalleProducto_deberiaGuardarAmbasEntidadesRelacionadas() {
        // Given
        when(detalleProductoRepository.save(detalleValido)).thenReturn(detalleValido);
        when(productoRepository.save(productoValido)).thenReturn(productoValido);

        // When
        Producto resultado = productoService.guardar(productoValido);

        // Then
        assertNotNull(resultado);
        assertEquals("Torta de Chocolate", resultado.getNombre());
        verify(detalleProductoRepository, times(1)).save(detalleValido);
        verify(productoRepository, times(1)).save(productoValido);
    }

    @Test
    void guardar_sinDetalleProducto_noDeberiaLlamarDetalleRepository() {
        // Given
        Producto productoSinDetalle = new Producto();
        productoSinDetalle.setNombre("Pan Amasado");
        productoSinDetalle.setPrecio(3000.0);
        productoSinDetalle.setStock(30);
        when(productoRepository.save(productoSinDetalle)).thenReturn(productoSinDetalle);

        // When
        Producto resultado = productoService.guardar(productoSinDetalle);

        // Then
        assertNotNull(resultado);
        verify(detalleProductoRepository, never()).save(any());
        verify(productoRepository, times(1)).save(productoSinDetalle);
    }

    @Test
    void listar_conProductosExistentes_deberiaRetornarListaCompleta() {
        // Given
        Producto otroProducto = new Producto();
        otroProducto.setId(2L);
        otroProducto.setNombre("Cheesecake");
        when(productoRepository.findAll()).thenReturn(Arrays.asList(productoValido, otroProducto));

        // When
        List<Producto> resultado = productoService.listar();

        // Then
        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_conIdExistente_deberiaRetornarProducto() {
        // Given
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoValido));

        // When
        Producto resultado = productoService.buscarPorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals("Torta de Chocolate", resultado.getNombre());
    }

    @Test
    void buscarPorId_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productoService.buscarPorId(99L));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    void existePorId_conIdExistente_deberiaRetornarTrue() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(true);

        // When
        boolean resultado = productoService.existePorId(1L);

        // Then
        assertTrue(resultado);
    }

    @Test
    void actualizar_conIdExistenteYDetalleExistente_deberiaActualizarAmbos() {
        // Given
        DetalleProducto nuevoDetalle = new DetalleProducto();
        nuevoDetalle.setDescripcion("Descripcion actualizada");
        nuevoDetalle.setContieneLactosa(false);
        nuevoDetalle.setContieneHuevos(false);
        nuevoDetalle.setContieneFrutosSecos(true);
        nuevoDetalle.setContieneGluten(true);

        Producto datosNuevos = new Producto();
        datosNuevos.setNombre("Torta Actualizada");
        datosNuevos.setPrecio(20000.0);
        datosNuevos.setStock(5);
        datosNuevos.setDetalleProducto(nuevoDetalle);

        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoValido));
        when(productoRepository.save(any(Producto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Producto resultado = productoService.actualizar(1L, datosNuevos);

        // Then
        assertEquals("Torta Actualizada", resultado.getNombre());
        assertEquals(20000.0, resultado.getPrecio());
        assertEquals("Descripcion actualizada", resultado.getDetalleProducto().getDescripcion());
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> productoService.actualizar(99L, productoValido));
        verify(productoRepository, never()).save(any());
    }

    @Test
    void eliminar_conIdExistente_deberiaEliminarProducto() {
        // Given
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        // When
        productoService.eliminar(1L);

        // Then
        verify(productoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_conIdInexistente_deberiaLanzarResourceNotFoundException() {
        // Given
        when(productoRepository.existsById(99L)).thenReturn(false);

        // When / Then
        assertThrows(ResourceNotFoundException.class,
                () -> productoService.eliminar(99L));
        verify(productoRepository, never()).deleteById(anyLong());
    }
}
