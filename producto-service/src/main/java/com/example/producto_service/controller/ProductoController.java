package com.example.producto_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDto) {
        logger.info("POST /productos - Creando producto: nombre={}", productoDto.getNombre());
        Producto nuevo = productoService.guardar(productoDto.toModel());
        logger.info("Producto creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        logger.info("GET /productos - Listando productos");
        List<ProductoDTO> dtos = productoService.listar().stream()
                .map(ProductoDTO::fromModel).collect(Collectors.toList());
        logger.info("Total productos listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /productos/{} - Obteniendo producto", id);
        return ResponseEntity.ok(ProductoDTO.fromModel(productoService.buscarPorId(id)));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<DetalleProductoDTO> obtenerDetalleProducto(@PathVariable Long id) {
        logger.info("GET /productos/{}/detalles - Obteniendo detalle", id);
        Producto producto = productoService.buscarPorId(id);
        return ResponseEntity.ok(DetalleProductoDTO.fromModel(producto.getDetalleProducto()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        logger.info("PUT /productos/{} - Actualizando producto", id);
        return ResponseEntity.ok(ProductoDTO.fromModel(productoService.actualizar(id, dto.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /productos/{} - Eliminando producto", id);
        productoService.eliminar(id);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeProducto(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.existePorId(id));
    }
}
