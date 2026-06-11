package com.example.empleado_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.empleado_service.dto.EmpleadoDTO;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.service.EmpleadoService;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

	@Autowired
	private EmpleadoService empleadoService;

	@PostMapping
	public ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO dto) {
		Empleado empleado = dto.toModel();
		Empleado guardado = empleadoService.guardar(empleado);
		return ResponseEntity.status(HttpStatus.CREATED).body(EmpleadoDTO.fromModel(guardado));
	}

	@GetMapping
	public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
		List<Empleado> empleados = empleadoService.listar();
		List<EmpleadoDTO> dtos = empleados.stream()
				.map(EmpleadoDTO::fromModel)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/{id}")
	public ResponseEntity<EmpleadoDTO> obtenerEmpleado(@PathVariable Long id) {
		Optional<Empleado> empleado = empleadoService.buscarPorId(id);
		return empleado.map(e -> ResponseEntity.ok(EmpleadoDTO.fromModel(e)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/rut/{rut}")
	public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorRut(@PathVariable String rut) {
		Optional<Empleado> empleado = empleadoService.buscarPorRut(rut);
		return empleado.map(e -> ResponseEntity.ok(EmpleadoDTO.fromModel(e)))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/cargo/{cargo}")
	public ResponseEntity<List<EmpleadoDTO>> obtenerEmpleadosPorCargo(@PathVariable String cargo) {
		List<Empleado> empleados = empleadoService.buscarPorCargo(cargo);
		List<EmpleadoDTO> dtos = empleados.stream()
				.map(EmpleadoDTO::fromModel)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@GetMapping("/activos")
	public ResponseEntity<List<EmpleadoDTO>> listarActivos() {
		List<Empleado> empleados = empleadoService.listarActivos();
		List<EmpleadoDTO> dtos = empleados.stream()
				.map(EmpleadoDTO::fromModel)
				.toList();
		return ResponseEntity.ok(dtos);
	}

	@PutMapping("/{id}")
	public ResponseEntity<EmpleadoDTO> actualizarEmpleado(@PathVariable Long id,
			@RequestBody EmpleadoDTO dto) {
		Empleado detalles = dto.toModel();
		Empleado actualizado = empleadoService.actualizar(id, detalles);
		if (actualizado != null) {
			return ResponseEntity.ok(EmpleadoDTO.fromModel(actualizado));
		}
		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
		if (empleadoService.eliminar(id)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

}
