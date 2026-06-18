package com.example.empleado_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.empleado_service.exception.ResourceNotFoundException;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.repository.EmpleadoRepository;
import java.util.List;

@Service
public class EmpleadoService {

    private static final Logger logger = LoggerFactory.getLogger(EmpleadoService.class);
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado guardar(Empleado empleado) {
        logger.info("Guardando empleado: nombre={}", empleado.getNombre());
        Empleado guardado = empleadoRepository.save(empleado);
        logger.info("Empleado guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<Empleado> listar() {
        logger.info("Listando todos los empleados");
        List<Empleado> empleados = empleadoRepository.findAll();
        logger.info("Total empleados: {}", empleados.size());
        return empleados;
    }

    public Empleado buscarPorId(Long id) {
        logger.info("Buscando empleado id={}", id);
        return empleadoRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Empleado no encontrado id={}", id);
                    return new ResourceNotFoundException("Empleado con id " + id + " no encontrado");
                });
    }

    public Empleado buscarPorRut(String rut) {
        logger.info("Buscando empleado rut={}", rut);
        return empleadoRepository.findByRutEmpleado(rut)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con rut " + rut + " no encontrado"));
    }

    public List<Empleado> buscarPorCargo(String cargo) {
        return empleadoRepository.findByCargo(cargo);
    }

    public List<Empleado> listarActivos() {
        return empleadoRepository.findByActivo(true);
    }

    public Empleado actualizar(Long id, Empleado detalles) {
        logger.info("Actualizando empleado id={}", id);
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado con id " + id + " no encontrado"));
        empleado.setCargo(detalles.getCargo());
        empleado.setSueldoBase(detalles.getSueldoBase());
        empleado.setValorHoraExtra(detalles.getValorHoraExtra());
        empleado.setActivo(detalles.getActivo());
        empleado.setTelefono(detalles.getTelefono());
        empleado.setDireccion(detalles.getDireccion());
        Empleado actualizado = empleadoRepository.save(empleado);
        logger.info("Empleado actualizado exitosamente id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando empleado id={}", id);
        if (!empleadoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Empleado con id " + id + " no encontrado");
        }
        empleadoRepository.deleteById(id);
        logger.info("Empleado eliminado exitosamente id={}", id);
    }
}
