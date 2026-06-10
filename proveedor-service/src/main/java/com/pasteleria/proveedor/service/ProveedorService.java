package com.pasteleria.proveedor.service;

import com.pasteleria.proveedor.model.Proveedor;
import com.pasteleria.proveedor.dto.ProveedorDTO;
import com.pasteleria.proveedor.repository.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorService {
    
    @Autowired
    private ProveedorRepository proveedorRepository;

    public ProveedorDTO guardar(ProveedorDTO proveedorDTO) {
        Proveedor proveedor = new Proveedor(
            proveedorDTO.getNombre(),
            proveedorDTO.getContacto(),
            proveedorDTO.getEmail(),
            proveedorDTO.getTelefono(),
            proveedorDTO.getDireccion(),
            proveedorDTO.getCiudad(),
            proveedorDTO.getPais()
        );
        Proveedor guardado = proveedorRepository.save(proveedor);
        return convertirADTO(guardado);
    }

    public List<ProveedorDTO> listar() {
        return proveedorRepository.findAll()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public List<ProveedorDTO> listarActivos() {
        return proveedorRepository.findByActivoTrue()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public Optional<ProveedorDTO> buscarPorId(Long id) {
        return proveedorRepository.findById(id)
            .map(this::convertirADTO);
    }

    public Optional<ProveedorDTO> buscarPorNombre(String nombre) {
        return proveedorRepository.findByNombreIgnoreCase(nombre)
            .map(this::convertirADTO);
    }

    public Optional<ProveedorDTO> buscarPorEmail(String email) {
        return proveedorRepository.findByEmailIgnoreCase(email)
            .map(this::convertirADTO);
    }

    public ProveedorDTO actualizar(Long id, ProveedorDTO proveedorDTO) {
        Optional<Proveedor> existente = proveedorRepository.findById(id);
        if (existente.isPresent()) {
            Proveedor proveedor = existente.get();
            proveedor.setNombre(proveedorDTO.getNombre());
            proveedor.setContacto(proveedorDTO.getContacto());
            proveedor.setEmail(proveedorDTO.getEmail());
            proveedor.setTelefono(proveedorDTO.getTelefono());
            proveedor.setDireccion(proveedorDTO.getDireccion());
            proveedor.setCiudad(proveedorDTO.getCiudad());
            proveedor.setPais(proveedorDTO.getPais());
            if (proveedorDTO.getActivo() != null) {
                proveedor.setActivo(proveedorDTO.getActivo());
            }
            return convertirADTO(proveedorRepository.save(proveedor));
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existePorId(Long id) {
        return proveedorRepository.existsById(id);
    }

    private ProveedorDTO convertirADTO(Proveedor proveedor) {
        return new ProveedorDTO(
            proveedor.getId(),
            proveedor.getNombre(),
            proveedor.getContacto(),
            proveedor.getEmail(),
            proveedor.getTelefono(),
            proveedor.getDireccion(),
            proveedor.getCiudad(),
            proveedor.getPais(),
            proveedor.getActivo()
        );
    }
}
