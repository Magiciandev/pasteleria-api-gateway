package com.example.asistencia_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.asistencia_service.dto.AsistenciaDTO;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.service.AsistenciaService;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/asistencias")
public class AsistenciaController {

    private static final Logger logger = LoggerFactory.getLogger(AsistenciaController.class);
    private final AsistenciaService asistenciaService;

    public AsistenciaController(AsistenciaService asistenciaService) {
        this.asistenciaService = asistenciaService;
    }

    @PostMapping
    public ResponseEntity<AsistenciaDTO> crearAsistencia(@Valid @RequestBody AsistenciaDTO dto) {
        logger.info("POST /asistencias - Registrando asistencia");
        
        Asistencia nueva = asistenciaService.registrar(dto.toModel());
        
        logger.info("Asistencia registrada exitosamente id={}", nueva.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AsistenciaDTO.fromModel(nueva));
    }

    @GetMapping
    public ResponseEntity<List<AsistenciaDTO>> listarAsistencias() {
        logger.info("GET /asistencias - Listando asistencias");
        List<AsistenciaDTO> dtos = asistenciaService.listar().stream()
                .map(AsistenciaDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> obtenerPorId(@PathVariable Long id) {
        logger.info("GET /asistencias/{} - Obteniendo registro de asistencia", id);
        try {
            Asistencia asistencia = asistenciaService.buscarPorId(id);
            if (asistencia == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(AsistenciaDTO.fromModel(asistencia));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> actualizar(@PathVariable Long id, @Valid @RequestBody AsistenciaDTO dto) {
        logger.info("PUT /asistencias/{} - Actualizando registro de asistencia", id);
        try {
            Asistencia actualizada = asistenciaService.actualizar(id, dto.toModel());
            if (actualizada == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.ok(AsistenciaDTO.fromModel(actualizada));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        logger.info("DELETE /asistencias/{} - Eliminando registro de asistencia", id);
        try {
            Asistencia asistencia = asistenciaService.buscarPorId(id);
            if (asistencia == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Registro de asistencia no encontrado");
            }
            
            asistenciaService.eliminar(id);
            logger.info("Asistencia eliminada exitosamente id={}", id);
            return ResponseEntity.ok("Asistencia eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al eliminar el registro de asistencia");
        }
    }
}