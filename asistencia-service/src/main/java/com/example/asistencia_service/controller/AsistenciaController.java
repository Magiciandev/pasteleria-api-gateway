package com.example.asistencia_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.asistencia_service.dto.AsistenciaDTO;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.service.AsistenciaService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
    public ResponseEntity<AsistenciaDTO> registrarAsistencia(@RequestBody AsistenciaDTO dto) {
        logger.info("POST /asistencias - Registrando: idEmpleado={}", dto.getIdEmpleado());
        Asistencia registrada = asistenciaService.registrar(dto.toModel());
        logger.info("Asistencia registrada exitosamente id={}", registrada.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(AsistenciaDTO.fromModel(registrada));
    }

    @GetMapping
    public ResponseEntity<List<AsistenciaDTO>> listarAsistencias() {
        logger.info("GET /asistencias - Listando asistencias");
        List<AsistenciaDTO> dtos = asistenciaService.listar().stream()
                .map(AsistenciaDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> obtenerAsistencia(@PathVariable Long id) {
        logger.info("GET /asistencias/{} - Obteniendo asistencia", id);
        return ResponseEntity.ok(AsistenciaDTO.fromModel(asistenciaService.buscarPorId(id)));
    }

    @GetMapping("/empleado/{idEmpleado}")
    public ResponseEntity<List<AsistenciaDTO>> obtenerPorEmpleado(@PathVariable Long idEmpleado) {
        List<AsistenciaDTO> dtos = asistenciaService.buscarPorEmpleado(idEmpleado).stream()
                .map(AsistenciaDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/fecha/{fecha}")
    public ResponseEntity<List<AsistenciaDTO>> obtenerPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        List<AsistenciaDTO> dtos = asistenciaService.buscarPorFecha(fecha).stream()
                .map(AsistenciaDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/empleado/{idEmpleado}/fecha/{fecha}")
    public ResponseEntity<AsistenciaDTO> obtenerPorEmpleadoYFecha(
            @PathVariable Long idEmpleado,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        Optional<Asistencia> asistencia = asistenciaService.buscarPorEmpleadoYFecha(idEmpleado, fecha);
        return asistencia.map(a -> ResponseEntity.ok(AsistenciaDTO.fromModel(a)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AsistenciaDTO> actualizarAsistencia(@PathVariable Long id, @RequestBody AsistenciaDTO dto) {
        logger.info("PUT /asistencias/{} - Actualizando asistencia", id);
        return ResponseEntity.ok(AsistenciaDTO.fromModel(asistenciaService.actualizar(id, dto.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarAsistencia(@PathVariable Long id) {
        logger.info("DELETE /asistencias/{} - Eliminando asistencia", id);
        asistenciaService.eliminar(id);
        return ResponseEntity.ok("Asistencia eliminada exitosamente");
    }
}
