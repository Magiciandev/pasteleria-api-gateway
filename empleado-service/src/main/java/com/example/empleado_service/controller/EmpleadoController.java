package com.example.empleado_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.empleado_service.dto.EmpleadoDTO;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.service.EmpleadoService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoController.class);
    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@Valid @RequestBody EmpleadoDTO dto) {
        logger.info("POST /empleados - Creando empleado");
        Empleado nuevo = empleadoService.guardar(dto.toModel());
        logger.info("Empleado creado exitosamente id={}", nuevo.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpleadoDTO.fromModel(nuevo));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
        logger.info("GET /empleados - Listando empleados");
        List<EmpleadoDTO> dtos = empleadoService.listar().stream()
                .map(EmpleadoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /empleados/{} - Obteniendo empleado", id);
        try {
            Empleado empleado = empleadoService.buscarPorId(id);
            if (empleado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(EmpleadoDTO.fromModel(empleado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizar(@PathVariable Long id, @Valid @RequestBody EmpleadoDTO dto) {
        logger.info("PUT /empleados/{} - Actualizando empleado", id);
        try {
            Empleado actualizado = empleadoService.actualizar(id, dto.toModel());
            if (actualizado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(EmpleadoDTO.fromModel(actualizado));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /empleados/{} - Eliminando empleado", id);
        try {
            Empleado empleado = empleadoService.buscarPorId(id);
            if (empleado == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Empleado no encontrado");
            }
            
            empleadoService.eliminar(id);
            logger.info("Empleado eliminado exitosamente id={}", id);
            return ResponseEntity.ok("Empleado eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el empleado");
        }
    }
}