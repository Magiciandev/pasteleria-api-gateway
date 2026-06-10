package com.pasteleria.ingrediente.service;

import com.pasteleria.ingrediente.model.Ingrediente;
import com.pasteleria.ingrediente.dto.IngredienteDTO;
import com.pasteleria.ingrediente.repository.IngredienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredienteService {
    
    @Autowired
    private IngredienteRepository ingredienteRepository;

    public IngredienteDTO guardar(IngredienteDTO ingredienteDTO) {
        Ingrediente ingrediente = new Ingrediente(
            ingredienteDTO.getNombre(),
            ingredienteDTO.getDescripcion(),
            ingredienteDTO.getCantidad(),
            ingredienteDTO.getUnidadMedida(),
            ingredienteDTO.getPrecioUnitario()
        );
        Ingrediente guardado = ingredienteRepository.save(ingrediente);
        return convertirADTO(guardado);
    }

    public List<IngredienteDTO> listar() {
        return ingredienteRepository.findAll()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public List<IngredienteDTO> listarActivos() {
        return ingredienteRepository.findByActivoTrue()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public Optional<IngredienteDTO> buscarPorId(Long id) {
        return ingredienteRepository.findById(id)
            .map(this::convertirADTO);
    }

    public Optional<IngredienteDTO> buscarPorNombre(String nombre) {
        return ingredienteRepository.findByNombreIgnoreCase(nombre)
            .map(this::convertirADTO);
    }

    public IngredienteDTO actualizar(Long id, IngredienteDTO ingredienteDTO) {
        Optional<Ingrediente> existente = ingredienteRepository.findById(id);
        if (existente.isPresent()) {
            Ingrediente ingrediente = existente.get();
            ingrediente.setNombre(ingredienteDTO.getNombre());
            ingrediente.setDescripcion(ingredienteDTO.getDescripcion());
            ingrediente.setCantidad(ingredienteDTO.getCantidad());
            ingrediente.setUnidadMedida(ingredienteDTO.getUnidadMedida());
            ingrediente.setPrecioUnitario(ingredienteDTO.getPrecioUnitario());
            if (ingredienteDTO.getActivo() != null) {
                ingrediente.setActivo(ingredienteDTO.getActivo());
            }
            return convertirADTO(ingredienteRepository.save(ingrediente));
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (ingredienteRepository.existsById(id)) {
            ingredienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existePorId(Long id) {
        return ingredienteRepository.existsById(id);
    }

    private IngredienteDTO convertirADTO(Ingrediente ingrediente) {
        return new IngredienteDTO(
            ingrediente.getId(),
            ingrediente.getNombre(),
            ingrediente.getDescripcion(),
            ingrediente.getCantidad(),
            ingrediente.getUnidadMedida(),
            ingrediente.getPrecioUnitario(),
            ingrediente.getActivo()
        );
    }
}
