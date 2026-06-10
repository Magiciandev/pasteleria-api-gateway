package com.pasteleria.inventario.service;

import com.pasteleria.inventario.model.InventarioItem;
import com.pasteleria.inventario.dto.InventarioItemDTO;
import com.pasteleria.inventario.repository.InventarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventarioService {
    
    @Autowired
    private InventarioRepository inventarioRepository;
    
    @Autowired
    private WebClient webClient;

    public InventarioItemDTO guardar(InventarioItemDTO inventarioDTO) {
        // Validar que el ingrediente existe
        if (!validarIngrediente(inventarioDTO.getIngredienteId())) {
            throw new RuntimeException("Ingrediente no encontrado");
        }
        
        // Validar que el proveedor existe
        if (!validarProveedor(inventarioDTO.getProveedorId())) {
            throw new RuntimeException("Proveedor no encontrado");
        }

        InventarioItem item = new InventarioItem(
            inventarioDTO.getIngredienteId(),
            inventarioDTO.getProveedorId(),
            inventarioDTO.getStockActual(),
            inventarioDTO.getStockMinimo(),
            inventarioDTO.getStockMaximo(),
            inventarioDTO.getPrecioCompra(),
            inventarioDTO.getUnidadMedida()
        );
        InventarioItem guardado = inventarioRepository.save(item);
        return convertirADTO(guardado);
    }

    public List<InventarioItemDTO> listar() {
        return inventarioRepository.findAll()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public List<InventarioItemDTO> listarActivos() {
        return inventarioRepository.findByActivoTrue()
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public Optional<InventarioItemDTO> buscarPorId(Long id) {
        return inventarioRepository.findById(id)
            .map(this::convertirADTO);
    }

    public List<InventarioItemDTO> buscarPorIngrediente(Long ingredienteId) {
        return inventarioRepository.findByIngredienteId(ingredienteId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public List<InventarioItemDTO> buscarPorProveedor(Long proveedorId) {
        return inventarioRepository.findByProveedorId(proveedorId)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public Optional<InventarioItemDTO> buscarPorIngredienteYProveedor(Long ingredienteId, Long proveedorId) {
        return inventarioRepository.findByIngredienteIdAndProveedorId(ingredienteId, proveedorId)
            .map(this::convertirADTO);
    }

    public List<InventarioItemDTO> obtenerStockBajo() {
        return inventarioRepository.findByStockActualLessThanEqual(0.0)
            .stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }

    public InventarioItemDTO actualizarStock(Long id, Double nuevoCantidad) {
        Optional<InventarioItem> existente = inventarioRepository.findById(id);
        if (existente.isPresent()) {
            InventarioItem item = existente.get();
            item.setStockActual(nuevoCantidad);
            return convertirADTO(inventarioRepository.save(item));
        }
        return null;
    }

    public InventarioItemDTO actualizar(Long id, InventarioItemDTO inventarioDTO) {
        Optional<InventarioItem> existente = inventarioRepository.findById(id);
        if (existente.isPresent()) {
            InventarioItem item = existente.get();
            item.setIngredienteId(inventarioDTO.getIngredienteId());
            item.setProveedorId(inventarioDTO.getProveedorId());
            item.setStockActual(inventarioDTO.getStockActual());
            item.setStockMinimo(inventarioDTO.getStockMinimo());
            item.setStockMaximo(inventarioDTO.getStockMaximo());
            item.setPrecioCompra(inventarioDTO.getPrecioCompra());
            item.setUnidadMedida(inventarioDTO.getUnidadMedida());
            if (inventarioDTO.getActivo() != null) {
                item.setActivo(inventarioDTO.getActivo());
            }
            return convertirADTO(inventarioRepository.save(item));
        }
        return null;
    }

    public boolean eliminar(Long id) {
        if (inventarioRepository.existsById(id)) {
            inventarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean existePorId(Long id) {
        return inventarioRepository.existsById(id);
    }

    private boolean validarIngrediente(Long ingredienteId) {
        try {
            Boolean existe = webClient.get()
                .uri("http://localhost:9098/ingredientes/{id}/exists", ingredienteId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validarProveedor(Long proveedorId) {
        try {
            Boolean existe = webClient.get()
                .uri("http://localhost:9099/proveedores/{id}/exists", proveedorId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
            return existe != null && existe;
        } catch (Exception e) {
            return false;
        }
    }

    private InventarioItemDTO convertirADTO(InventarioItem item) {
        return new InventarioItemDTO(
            item.getId(),
            item.getIngredienteId(),
            item.getProveedorId(),
            item.getStockActual(),
            item.getStockMinimo(),
            item.getStockMaximo(),
            item.getPrecioCompra(),
            item.getUnidadMedida(),
            item.getActivo()
        );
    }
}
