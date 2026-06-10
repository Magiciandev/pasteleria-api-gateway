package com.example.envio_service.controller;

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

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    /**
     * POST /envios - Crear un nuevo envío
     */
    @PostMapping
    public ResponseEntity<?> crearEnvio(@RequestBody EnvioDTO envioDto) {
        try {
            Envio nuevo = envioService.crearEnvio(envioDto.toModel());
            return ResponseEntity.status(HttpStatus.CREATED).body(EnvioDTO.fromModel(nuevo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error al crear envío: " + e.getMessage()));
        }
    }

    /**
     * GET /envios - Listar todos los envíos
     */
    @GetMapping
    public ResponseEntity<List<EnvioDTO>> listar() {
        List<EnvioDTO> dtos = envioService.listar().stream()
            .map(EnvioDTO::fromModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /envios/{id} - Obtener envío por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Envio e = envioService.buscarPorId(id);
        if (e != null) {
            return ResponseEntity.ok(EnvioDTO.fromModel(e));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /envios/cliente/{idCliente} - Obtener envíos por cliente
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<EnvioDTO>> obtenerPorCliente(@PathVariable Long idCliente) {
        List<EnvioDTO> dtos = envioService.buscarPorCliente(idCliente).stream()
            .map(EnvioDTO::fromModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /envios/pedido/{idPedido} - Obtener envío por pedido
     */
    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<?> obtenerPorPedido(@PathVariable Long idPedido) {
        Optional<Envio> envio = envioService.buscarPorPedido(idPedido);
        if (envio.isPresent()) {
            return ResponseEntity.ok(EnvioDTO.fromModel(envio.get()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * PUT /envios/{id}/estado - Actualizar solo el estado del envío
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody EstadoRequest request) {
        try {
            Envio actualizado = envioService.actualizarEstado(id, request.getEstado());
            if (actualizado != null) {
                return ResponseEntity.ok(EnvioDTO.fromModel(actualizado));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error al actualizar estado: " + e.getMessage()));
        }
    }

    /**
     * DELETE /envios/{id} - Eliminar envío
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        envioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clase auxiliar para solicitudes de actualización de estado
     */
    public static class EstadoRequest {
        private String estado;

        public EstadoRequest() {
        }

        public EstadoRequest(String estado) {
            this.estado = estado;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }

    /**
     * Clase auxiliar para respuestas de error
     */
    public static class ErrorResponse {
        private String mensaje;

        public ErrorResponse(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }
    }
}
