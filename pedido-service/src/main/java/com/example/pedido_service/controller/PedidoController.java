package com.example.pedido_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pedido_service.dto.PedidoDTO;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.service.PedidoService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/idd/pedidos")
public class PedidoController {

    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(@RequestBody PedidoDTO pedidoDto) {
        logger.info("POST /pedidos - Creando pedido: idCliente={}", pedidoDto.getIdCliente());
        Pedido nuevo = pedidoService.guardar(pedidoDto.toModel());
        logger.info("Pedido creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PedidoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarPedidos() {
        logger.info("GET /pedidos - Listando pedidos");
        List<PedidoDTO> dtos = pedidoService.listar().stream()
                .map(PedidoDTO::fromModel).collect(Collectors.toList());
        logger.info("Total pedidos listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/idd/{id}")
    public ResponseEntity<PedidoDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /pedidos/{} - Obteniendo pedido", id);
        return ResponseEntity.ok(PedidoDTO.fromModel(pedidoService.buscarPorId(id)));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<PedidoDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        List<PedidoDTO> dtos = pedidoService.buscarPorIdCliente(idCliente).stream()
                .map(PedidoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoDTO> actualizar(@PathVariable Long id, @RequestBody PedidoDTO dto) {
        logger.info("PUT /pedidos/{} - Actualizando pedido", id);
        return ResponseEntity.ok(PedidoDTO.fromModel(pedidoService.actualizar(id, dto.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /pedidos/{} - Eliminando pedido", id);
        pedidoService.eliminar(id);
        return ResponseEntity.ok("Pedido eliminado exitosamente");
    }

    @GetMapping("/total")
    public ResponseEntity<Long> obtenerTotalPedidos() {
        return ResponseEntity.ok((long) pedidoService.listar().size());
    }
}
