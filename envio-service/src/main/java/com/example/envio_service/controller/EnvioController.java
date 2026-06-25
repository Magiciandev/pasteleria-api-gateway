package com.example.envio_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.envio_service.model.Envio;
import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.service.EnvioService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private static final Logger logger = LoggerFactory.getLogger(EnvioController.class);
    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @PostMapping
    public ResponseEntity<EnvioDTO> crearEnvio(@Valid @RequestBody EnvioDTO envioDto) {
        logger.info("POST /envios - Creando envio: idPedido={}, idCliente={}", envioDto.getIdPedido(), envioDto.getIdCliente());
        Envio nuevo = envioService.crearEnvio(envioDto.toModel());
        logger.info("Envio creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvioDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> listar() {
        logger.info("GET /envios - Listando envios");
        List<EnvioDTO> dtos = envioService.listar().stream()
                .map(EnvioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /envios/{} - Obteniendo envio", id);
        try {
            Envio envio = envioService.buscarPorId(id);
            if (envio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(EnvioDTO.fromModel(envio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<EnvioDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        logger.info("GET /envios/cliente/{} - Obteniendo envios por cliente", idCliente);
        List<EnvioDTO> dtos = envioService.buscarPorCliente(idCliente).stream()
                .map(EnvioDTO::fromModel).collect(Collectors.toList());
        if (dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<EnvioDTO> obtenerPorPedido(@PathVariable Long idPedido) {
        logger.info("GET /envios/pedido/{} - Obteniendo envio por pedido", idPedido);
        try {
            Optional<Envio> envio = envioService.buscarPorPedido(idPedido);
            return envio.map(e -> ResponseEntity.ok(EnvioDTO.fromModel(e)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        logger.info("PUT /envios/{}/estado - Actualizando a {}", id, request.getEstado());
        try {
            Envio actualizado = envioService.actualizarEstado(id, request.getEstado());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(EnvioDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /envios/{} - Eliminando envio", id);
        try {
            Envio envio = envioService.buscarPorId(id);
            if (envio == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Envio no encontrado");
            }
            
            envioService.eliminar(id);
            logger.info("Envio eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Envio eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el envio");
        }
    }

    public static class EstadoRequest {
        private String estado;
        public EstadoRequest() {}
        public EstadoRequest(String estado) { this.estado = estado; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}