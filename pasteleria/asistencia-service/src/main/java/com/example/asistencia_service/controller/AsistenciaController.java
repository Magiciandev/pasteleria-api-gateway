package com.example.asistencia_service.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.asistencia_service.dto.AsistenciaDTO;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.service.AsistenciaService;

@RestController
@RequestMapping("/asistencias")
public class AsistenciaController {

	@Autowired
	private AsistenciaService asistenciaService;

	@PostMapping
	public ResponseEntity<AsistenciaDTO> registrarAsistencia(@RequestBody AsistenciaDTO dto) {
		Asistencia asistencia = dto.toModel();
		Asistencia registrada = asistenciaService.registrar(asistencia);
		return ResponseEntity.status(HttpStatus.CREATED).body(AsistenciaDTO.fromModel(registrada));
	}

	@GetMapping
	public ResponseEntity<List<AsistenciaDTO>> listarAsistencias() {
		List<Asistencia> asistencias = asistenciaService.listar();
		List<AsistenciaDTO> dtos = asistencias.stream()
				.map(AsistenciaDTO::fromModel)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AsistenciaDTO> obtenerAsistencia(@PathVariable Long id) {
		Optional<Asistencia> asistencia = asistenciaService.buscarPorId(id);
		return asistencia.map(a -> ResponseEntity.ok(AsistenciaDTO.fromModel(a)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/empleado/{idEmpleado}")
	public ResponseEntity<List<AsistenciaDTO>> obtenerPorEmpleado(@PathVariable Long idEmpleado) {
		List<Asistencia> asistencias = asistenciaService.buscarPorEmpleado(idEmpleado);
		List<AsistenciaDTO> dtos = asistencias.stream()
				.map(AsistenciaDTO::fromModel)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<List<AsistenciaDTO>> obtenerPorFecha(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
		List<Asistencia> asistencias = asistenciaService.buscarPorFecha(fecha);
		List<AsistenciaDTO> dtos = asistencias.stream()
				.map(AsistenciaDTO::fromModel)
				.toList();
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
	public ResponseEntity<AsistenciaDTO> actualizarAsistencia(@PathVariable Long id,
			@RequestBody AsistenciaDTO dto) {
		Asistencia detalles = dto.toModel();
		Asistencia actualizada = asistenciaService.actualizar(id, detalles);
		if (actualizada != null) {
			return ResponseEntity.ok(AsistenciaDTO.fromModel(actualizada));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarAsistencia(@PathVariable Long id) {
		if (asistenciaService.eliminar(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

}
