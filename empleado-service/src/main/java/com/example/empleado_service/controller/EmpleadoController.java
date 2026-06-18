package com.example.empleado_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.empleado_service.dto.EmpleadoDTO;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.service.EmpleadoService;
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
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO dto) {
        logger.info("POST /empleados - Creando empleado: nombre={}", dto.getNombre());
        Empleado guardado = empleadoService.guardar(dto.toModel());
        logger.info("Empleado creado exitosamente id={}", guardado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(EmpleadoDTO.fromModel(guardado));
    }

    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
        logger.info("GET /empleados - Listando empleados");
        List<EmpleadoDTO> dtos = empleadoService.listar().stream()
                .map(EmpleadoDTO::fromModel).collect(Collectors.toList());
        logger.info("Total empleados listados: {}", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Long id) {
        logger.info("GET /empleados/{} - Obteniendo empleado", id);
        return ResponseEntity.ok(EmpleadoDTO.fromModel(empleadoService.buscarPorId(id)));
    }

    @GetMapping("/rut/{rut}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorRut(@PathVariable String rut) {
        logger.info("GET /empleados/rut/{} - Buscando empleado", rut);
        return ResponseEntity.ok(EmpleadoDTO.fromModel(empleadoService.buscarPorRut(rut)));
    }

    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleadosPorCargo(@PathVariable String cargo) {
        List<EmpleadoDTO> dtos = empleadoService.buscarPorCargo(cargo).stream()
                .map(EmpleadoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoDTO>> listarActivos() {
        List<EmpleadoDTO> dtos = empleadoService.listarActivos().stream()
                .map(EmpleadoDTO::fromModel).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id, @RequestBody EmpleadoDTO dto) {
        logger.info("PUT /empleados/{} - Actualizando empleado", id);
        return ResponseEntity.ok(EmpleadoDTO.fromModel(empleadoService.actualizar(id, dto.toModel())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarEmpleado(@PathVariable Long id) {
        logger.info("DELETE /empleados/{} - Eliminando empleado", id);
        empleadoService.eliminar(id);
        return ResponseEntity.ok("Empleado eliminado exitosamente");
    }
}
