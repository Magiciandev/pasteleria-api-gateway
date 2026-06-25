package com.example.pedido_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pedido_service.dto.PedidoDTO;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.service.PedidoService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@Valid @RequestBody PedidoDTO dto) {
        logger.info("POST /pedidos - Creando pedido");
        Pedido nuevo = pedidoService.guardar(dto.toModel());
        logger.info("Pedido creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        logger.info("GET /pedidos - Listando pedidos");
        List<PedidoDTO> dtos = pedidoService.listar().stream()
                .map(PedidoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /pedidos/{} - Obteniendo pedido", id);
        try {
            Pedido pedido = pedidoService.buscarPorId(id);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(PedidoDTO.fromModel(pedido));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PedidoDTO dto) {
        logger.info("PUT /pedidos/{} - Actualizando pedido", id);
        try {
            Pedido actualizado = pedidoService.actualizar(id, dto.toModel());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(PedidoDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /pedidos/{} - Eliminando pedido", id);
        try {
            // Usamos buscarPorId en lugar de existePorId para evitar el error de compilación
            Pedido pedido = pedidoService.buscarPorId(id);
            if (pedido == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pedido no encontrado");
            }
            
            pedidoService.eliminar(id);
            logger.info("Pedido eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Pedido eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el pedido");
        }
    }
}