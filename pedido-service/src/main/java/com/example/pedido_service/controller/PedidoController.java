package com.example.pedido_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pedido_service.dto.PedidoDTO;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.service.PedidoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /**
     * POST: Crear pedido
     * Valida que cliente y productos existan
     * Obtiene nombre del cliente y precios de productos desde microservicios
     */
    @PostMapping
    public ResponseEntity<?> crearPedido(@RequestBody PedidoDTO pedidoDto) {
        try {
            Pedido nuevoPedido = pedidoService.guardar(pedidoDto.toModel());
            return ResponseEntity.status(HttpStatus.CREATED).body(PedidoDTO.fromModel(nuevoPedido));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear pedido: " + e.getMessage());
        }
    }

    /**
     * GET: Listar todos los pedidos
     */
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        List<PedidoDTO> dtos = pedidoService.listar().stream()
                .map(PedidoDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET: Obtener pedido por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.buscarPorId(id);
        return pedido != null ? ResponseEntity.ok(PedidoDTO.fromModel(pedido)) : ResponseEntity.notFound().build();
    }

    /**
     * GET: Obtener pedidos por ID de cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        List<PedidoDTO> dtos = pedidoService.buscarPorIdCliente(idCliente).stream()
                .map(PedidoDTO::fromModel)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * PUT: Actualizar pedido
     */
    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @RequestBody PedidoDTO dto) {
        Pedido actualizado = pedidoService.actualizar(id, dto.toModel());
        return actualizado != null ? ResponseEntity.ok(PedidoDTO.fromModel(actualizado)) : ResponseEntity.notFound().build();
    }

    /**
     * DELETE: Eliminar pedido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET: Total de pedidos
     */
    @GetMapping("/total")
    public ResponseEntity<Long> obtenerTotalPedidos() {
        long total = pedidoService.listar().size();
        return ResponseEntity.ok(total);
    }
}