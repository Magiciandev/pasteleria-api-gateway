package com.example.cliente_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.cliente_service.dto.ClienteDTO;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.service.ClienteService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteController.class);
    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<ClienteDTO> crearCliente(@Valid @RequestBody ClienteDTO clienteDto) {
        logger.info("POST /clientes - Creando cliente: correo={}", clienteDto.getCorreo());
        Cliente nuevo = clienteService.guardar(clienteDto.toModel());
        logger.info("Cliente creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listarClientes() {
        logger.info("GET /clientes - Listando clientes");
        List<ClienteDTO> dtos = clienteService.listar().stream()
                .map(ClienteDTO::fromModel).collect(Collectors.toList());
        logger.info("Total clientes listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /clientes/{} - Obteniendo cliente", id);
        try {
            Cliente cliente = clienteService.buscarPorId(id);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ClienteDTO.fromModel(cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClienteDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ClienteDTO dto) {
        logger.info("PUT /clientes/{} - Actualizando cliente", id);
        try {
            Cliente actualizado = clienteService.actualizar(id, dto.toModel());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ClienteDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /clientes/{} - Eliminando cliente", id);
        try {
            if (!clienteService.existePorId(id)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado");
            }
            clienteService.eliminar(id);
            logger.info("Cliente eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Cliente eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar");
        }
    }

    @GetMapping("/total")
    public ResponseEntity<Long> obtenerTotalClientes() {
        long total = clienteService.listar().size();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/correo/{correo}")
    public ResponseEntity<ClienteDTO> buscarPorCorreo(@PathVariable String correo) {
        logger.info("GET /clientes/correo/{} - Buscando cliente", correo);
        try {
            Cliente cliente = clienteService.buscarPorCorreo(correo);
            if (cliente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(ClienteDTO.fromModel(cliente));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<List<ClienteDTO>> buscarPorNombre(@PathVariable String nombre) {
        logger.info("GET /clientes/nombre/{} - Buscando clientes", nombre);
        List<ClienteDTO> dtos = clienteService.buscarPorNombre(nombre).stream()
                .map(ClienteDTO::fromModel).collect(Collectors.toList());
        if (dtos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> existeCliente(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.existePorId(id));
    }
}