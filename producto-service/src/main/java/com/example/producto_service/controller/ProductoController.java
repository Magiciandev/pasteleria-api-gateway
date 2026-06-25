package com.example.producto_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.producto_service.dto.ProductoDTO;
import com.example.producto_service.model.Producto;
import com.example.producto_service.service.ProductoService;
import jakarta.validation.Valid;

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
    public ResponseEntity<ProductoDTO> crearProducto(@Valid @RequestBody ProductoDTO dto) {
        logger.info("POST /productos - Creando producto");
        Producto nuevo = productoService.guardar(dto.toModel());
        logger.info("Producto creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarProductos() {
        logger.info("GET /productos - Listando productos");
        List<ProductoDTO> dtos = productoService.listar().stream()
                .map(ProductoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /productos/{} - Obteniendo producto", id);
        try {
            Producto producto = productoService.buscarPorId(id);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ProductoDTO.fromModel(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO dto) {
        logger.info("PUT /productos/{} - Actualizando producto", id);
        try {
            Producto actualizado = productoService.actualizar(id, dto.toModel());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ProductoDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /productos/{} - Eliminando producto", id);
        try {
            Producto producto = productoService.buscarPorId(id);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
            }
            
            productoService.eliminar(id);
            logger.info("Producto eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Producto eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el producto");
        }
    }
}