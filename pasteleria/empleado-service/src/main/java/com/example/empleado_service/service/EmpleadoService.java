package com.example.empleado_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.repository.EmpleadoRepository;

@Service
public class EmpleadoService {

	@Autowired
	private EmpleadoRepository empleadoRepository;

	public Empleado guardar(Empleado empleado) {
		return empleadoRepository.save(empleado);
	}

	public List<Empleado> listar() {
		return empleadoRepository.findAll();
	}

	public Optional<Empleado> buscarPorId(Long id) {
		return empleadoRepository.findById(id);
	}

	public Optional<Empleado> buscarPorRut(String rut) {
		return empleadoRepository.findByRutEmpleado(rut);
	}

	public List<Empleado> buscarPorCargo(String cargo) {
		return empleadoRepository.findByCargo(cargo);
	}

	public List<Empleado> listarActivos() {
		return empleadoRepository.findByActivo(true);
	}

	public Empleado actualizar(Long id, Empleado detalles) {
		Optional<Empleado> empleadoOpt = empleadoRepository.findById(id);
		if (empleadoOpt.isPresent()) {
			Empleado empleado = empleadoOpt.get();
			empleado.setCargo(detalles.getCargo());
			empleado.setSueldoBase(detalles.getSueldoBase());
			empleado.setValorHoraExtra(detalles.getValorHoraExtra());
			empleado.setActivo(detalles.getActivo());
			empleado.setTelefono(detalles.getTelefono());
			empleado.setDireccion(detalles.getDireccion());
			return empleadoRepository.save(empleado);
		}
		return null;
	}

	public boolean eliminar(Long id) {
		if (empleadoRepository.existsById(id)) {
			empleadoRepository.deleteById(id);
			return true;
		}
		return false;
	}

}
