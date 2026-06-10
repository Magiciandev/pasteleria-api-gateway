package com.example.producto_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.producto_service.dto.ProductoDTO;
import com.example.producto_service.dto.DetalleProductoDTO;
import com.example.producto_service.model.Producto;
import com.example.producto_service.service.ProductoService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // POST: Crear producto con detalles
    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDto) {
        Producto nuevo = productoService.guardar(productoDto.toModel());
        return ResponseEntity.ok(ProductoDTO.fromModel(nuevo));
    }

    // GET: Listar todos los productos
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        List<ProductoDTO> dtos = productoService.listar().stream()
                .map(ProductoDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // GET: Buscar producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        return producto != null ? ResponseEntity.ok(ProductoDTO.fromModel(producto)) : ResponseEntity.notFound().build();
    }

    // GET: Obtener detalles de un producto
    @GetMapping("/{id}/detalles")
    public ResponseEntity<DetalleProductoDTO> obtenerDetalleProducto(@PathVariable Long id) {
        Producto producto = productoService.buscarPorId(id);
        if (producto != null && producto.getDetalleProducto() != null) {
            return ResponseEntity.ok(DetalleProductoDTO.fromModel(producto.getDetalleProducto()));
        }
        return ResponseEntity.notFound().build();
    }

    // PUT: Actualizar producto
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        Producto actualizado = productoService.actualizar(id, dto.toModel());
        return actualizado != null ? ResponseEntity.ok(ProductoDTO.fromModel(actualizado)) : ResponseEntity.notFound().build();
    }

    // DELETE: Eliminar producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // GET: Validación para Pedido-Service
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.existePorId(id));
    }
}