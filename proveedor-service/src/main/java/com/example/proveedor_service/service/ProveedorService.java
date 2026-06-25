package com.example.proveedor_service.service;

import com.example.proveedor_service.exception.BadRequestException;
import com.example.proveedor_service.exception.ResourceNotFoundException;
import com.example.proveedor_service.model.Proveedor;
import com.example.proveedor_service.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorService.class);

    private final ProveedorRepository proveedorRepository;

    public ProveedorService(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public Proveedor guardar(Proveedor proveedor) {
        logger.info("Guardando proveedor: nombre={}", proveedor.getNombre());
        if (proveedorRepository.findByRut(proveedor.getRut()).isPresent()) {
            throw new BadRequestException("Ya existe un proveedor con el RUT " + proveedor.getRut());
        }
        Proveedor guardado = proveedorRepository.save(proveedor);
        logger.info("Proveedor guardado exitosamente id={}", guardado.getId());
        return guardado;
    }

    public List<Proveedor> listar() {
        logger.info("Listando todos los proveedores");
        List<Proveedor> proveedores = proveedorRepository.findAll();
        logger.info("Total proveedores: {}", proveedores.size());
        return proveedores;
    }

    public List<Proveedor> listarActivos() {
        logger.info("Listando proveedores activos");
        List<Proveedor> activos = proveedorRepository.findByActivo(true);
        logger.info("Total proveedores activos: {}", activos.size());
        return activos;
    }

    public Proveedor buscarPorId(Long id) {
        logger.info("Buscando proveedor id={}", id);
        return proveedorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Proveedor no encontrado id={}", id);
                    return new ResourceNotFoundException("Proveedor con id " + id + " no encontrado");
                });
    }

    public Proveedor buscarPorRut(String rut) {
        logger.info("Buscando proveedor rut={}", rut);
        return proveedorRepository.findByRut(rut)
                .orElseThrow(() -> {
                    logger.warn("Proveedor no encontrado rut={}", rut);
                    return new ResourceNotFoundException("Proveedor con RUT " + rut + " no encontrado");
                });
    }

    public boolean existePorId(Long id) {
        return proveedorRepository.existsById(id);
    }

    public Proveedor actualizar(Long id, Proveedor detalles) {
        logger.info("Actualizando proveedor id={}", id);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor con id " + id + " no encontrado"));

        if (!proveedor.getRut().equals(detalles.getRut())
                && proveedorRepository.findByRut(detalles.getRut()).isPresent()) {
            throw new BadRequestException("Ya existe un proveedor con el RUT " + detalles.getRut());
        }

        proveedor.setNombre(detalles.getNombre());
        proveedor.setRut(detalles.getRut());
        proveedor.setCorreo(detalles.getCorreo());
        proveedor.setTelefono(detalles.getTelefono());
        proveedor.setDireccion(detalles.getDireccion());
        proveedor.setActivo(detalles.getActivo());
        proveedor.setFechaRegistro(detalles.getFechaRegistro());

        Proveedor actualizado = proveedorRepository.save(proveedor);
        logger.info("Proveedor actualizado exitosamente id={}", id);
        return actualizado;
    }

    public Proveedor cambiarEstado(Long id, Boolean activo) {
        logger.info("Cambiando estado de proveedor id={} a activo={}", id, activo);
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor con id " + id + " no encontrado"));
        proveedor.setActivo(activo);
        Proveedor actualizado = proveedorRepository.save(proveedor);
        logger.info("Estado de proveedor actualizado id={}", id);
        return actualizado;
    }

    public void eliminar(Long id) {
        logger.info("Eliminando proveedor id={}", id);
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor con id " + id + " no encontrado");
        }
        proveedorRepository.deleteById(id);
        logger.info("Proveedor eliminado exitosamente id={}", id);
    }
}
