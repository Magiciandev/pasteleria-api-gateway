package com.example.compra_service.controller;

import com.example.compra_service.dto.CompraDTO;
import com.example.compra_service.model.Compra;
import com.example.compra_service.service.CompraService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compras")
public class CompraController {

    private static final Logger logger = LoggerFactory.getLogger(CompraController.class);
    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping
    public ResponseEntity<CompraDTO> crearCompra(@Valid @RequestBody CompraDTO dto) {
        logger.info("POST /compras - Creando compra");
        Compra nueva = compraService.guardar(dto.toModel());
        logger.info("Compra creada exitosamente id={}", nueva.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(CompraDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<CompraDTO>> listarCompras() {
        logger.info("GET /compras - Listando compras");
        List<CompraDTO> dtos = compraService.listar().stream()
                .map(CompraDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /compras/{} - Obteniendo compra", id);
        Compra compra = compraService.buscarPorId(id);
        return ResponseEntity.ok(CompraDTO.fromModel(compra));
    }

    @GetMapping("/proveedor/{idProveedor}")
    public ResponseEntity<List<CompraDTO>> listarPorProveedor(@PathVariable Long idProveedor) {
        logger.info("GET /compras/proveedor/{} - Listando compras por proveedor", idProveedor);
        List<CompraDTO> dtos = compraService.buscarPorProveedor(idProveedor).stream()
                .map(CompraDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CompraDTO>> listarPorEstado(@PathVariable String estado) {
        logger.info("GET /compras/estado/{} - Listando compras por estado", estado);
        List<CompraDTO> dtos = compraService.buscarPorEstado(estado.toUpperCase()).stream()
                .map(CompraDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/recibir")
    public ResponseEntity<CompraDTO> recibirCompra(@PathVariable Long id) {
        logger.info("PATCH /compras/{}/recibir - Recibiendo compra", id);
        Compra actualizada = compraService.recibir(id);
        return ResponseEntity.ok(CompraDTO.fromModel(actualizada));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<CompraDTO> anularCompra(@PathVariable Long id) {
        logger.info("PATCH /compras/{}/anular - Anulando compra", id);
        Compra actualizada = compraService.anular(id);
        return ResponseEntity.ok(CompraDTO.fromModel(actualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /compras/{} - Eliminando compra", id);
        compraService.eliminar(id);
        logger.info("Compra eliminada exitosamente id={}", id);
        return ResponseEntity.ok("Compra eliminada exitosamente");
    }
}
