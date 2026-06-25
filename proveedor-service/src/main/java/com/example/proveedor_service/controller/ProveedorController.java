package com.example.proveedor_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.proveedor_service.dto.ProveedorDTO;
import com.example.proveedor_service.model.Proveedor;
import com.example.proveedor_service.service.ProveedorService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    @PostMapping
    public ResponseEntity<ProveedorDTO> crearProveedor(@Valid @RequestBody ProveedorDTO dto) {
        logger.info("POST /proveedores - Creando proveedor");
        Proveedor nuevo = proveedorService.guardar(dto.toModel());
        logger.info("Proveedor creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProveedorDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
        logger.info("GET /proveedores - Listando proveedores");
        List<ProveedorDTO> dtos = proveedorService.listar().stream()
                .map(ProveedorDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /proveedores/{} - Obteniendo proveedor", id);
        try {
            Proveedor proveedor = proveedorService.buscarPorId(id);
            if (proveedor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ProveedorDTO.fromModel(proveedor));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ProveedorDTO dto) {
        logger.info("PUT /proveedores/{} - Actualizando proveedor", id);
        try {
            Proveedor actualizado = proveedorService.actualizar(id, dto.toModel());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ProveedorDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /proveedores/{} - Eliminando proveedor", id);
        try {
            Proveedor proveedor = proveedorService.buscarPorId(id);
            if (proveedor == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Proveedor no encontrado");
            }
            
            proveedorService.eliminar(id);
            logger.info("Proveedor eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Proveedor eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el proveedor");
        }
    }
}