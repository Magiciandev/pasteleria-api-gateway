package com.example.envio_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.envio_service.model.Envio;
import com.example.envio_service.dto.EnvioDTO;
import com.example.envio_service.service.EnvioService;
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
    public ResponseEntity<EnvioDTO> crearEnvio(@RequestBody EnvioDTO envioDto) {
        logger.info("POST /envios - Creando envio: idPedido={}, idCliente={}", envioDto.getIdPedido(), envioDto.getIdCliente());
        Envio nuevo = envioService.crearEnvio(envioDto.toModel());
        logger.info("Envio creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EnvioDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<EnvioDTO>> listar() {
        logger.info("GET /envios - Listando envios");
        List<EnvioDTO> dtos = envioService.listar().stream().map(EnvioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnvioDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /envios/{} - Obteniendo envio", id);
        return ResponseEntity.ok(EnvioDTO.fromModel(envioService.buscarPorId(id)));
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<EnvioDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        List<EnvioDTO> dtos = envioService.buscarPorCliente(idCliente).stream().map(EnvioDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<EnvioDTO> obtenerPorPedido(@PathVariable Long idPedido) {
        Optional<Envio> envio = envioService.buscarPorPedido(idPedido);
        return envio.map(e -> ResponseEntity.ok(EnvioDTO.fromModel(e)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioDTO> actualizarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        logger.info("PUT /envios/{}/estado - Actualizando a {}", id, request.getEstado());
        Envio actualizado = envioService.actualizarEstado(id, request.getEstado());
        return ResponseEntity.ok(EnvioDTO.fromModel(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /envios/{} - Eliminando envio", id);
        envioService.eliminar(id);
        return ResponseEntity.ok("Envio eliminado exitosamente");
    }

    public static class EstadoRequest {
        private String estado;
        public EstadoRequest() {}
        public EstadoRequest(String estado) { this.estado = estado; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}
