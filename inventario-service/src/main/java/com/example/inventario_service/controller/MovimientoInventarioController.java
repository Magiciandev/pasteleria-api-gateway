package com.example.inventario_service.controller;

import com.example.inventario_service.dto.MovimientoInventarioDTO;
import com.example.inventario_service.model.MovimientoInventario;
import com.example.inventario_service.service.MovimientoInventarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/inventario")
public class MovimientoInventarioController {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioController.class);
    private final MovimientoInventarioService inventarioService;

    public MovimientoInventarioController(MovimientoInventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping
    public ResponseEntity<MovimientoInventarioDTO> registrarMovimiento(@Valid @RequestBody MovimientoInventarioDTO dto) {
        logger.info("POST /inventario - Registrando movimiento de inventario");
        MovimientoInventario nuevo = inventarioService.registrar(dto.toModel());
        logger.info("Movimiento de inventario registrado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MovimientoInventarioDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoInventarioDTO>> listarMovimientos() {
        logger.info("GET /inventario - Listando movimientos de inventario");
        List<MovimientoInventarioDTO> dtos = inventarioService.listar().stream()
                .map(MovimientoInventarioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /inventario/{} - Obteniendo movimiento de inventario", id);
        MovimientoInventario movimiento = inventarioService.buscarPorId(id);
        return ResponseEntity.ok(MovimientoInventarioDTO.fromModel(movimiento));
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<MovimientoInventarioDTO>> listarPorProducto(@PathVariable Long idProducto) {
        logger.info("GET /inventario/producto/{} - Listando movimientos por producto", idProducto);
        List<MovimientoInventarioDTO> dtos = inventarioService.listarPorProducto(idProducto).stream()
                .map(MovimientoInventarioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimientoInventarioDTO>> listarPorTipo(@PathVariable String tipo) {
        logger.info("GET /inventario/tipo/{} - Listando movimientos por tipo", tipo);
        List<MovimientoInventarioDTO> dtos = inventarioService.listarPorTipo(tipo).stream()
                .map(MovimientoInventarioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MovimientoInventarioDTO> actualizar(@PathVariable Long id,
                                                               @Valid @RequestBody MovimientoInventarioDTO dto) {
        logger.info("PUT /inventario/{} - Actualizando movimiento de inventario", id);
        MovimientoInventario actualizado = inventarioService.actualizar(id, dto.toModel());
        return ResponseEntity.ok(MovimientoInventarioDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /inventario/{} - Eliminando movimiento de inventario", id);
        inventarioService.eliminar(id);
        logger.info("Movimiento de inventario eliminado exitosamente id={}", id);
        return ResponseEntity.ok("Movimiento de inventario eliminado exitosamente");
    }
}
