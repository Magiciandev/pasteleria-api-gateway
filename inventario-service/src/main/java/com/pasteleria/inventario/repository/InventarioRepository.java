package com.pasteleria.inventario.repository;

import com.pasteleria.inventario.model.InventarioItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioItem, Long> {
    Optional<InventarioItem> findByIngredienteIdAndProveedorId(Long ingredienteId, Long proveedorId);
    List<InventarioItem> findByIngredienteId(Long ingredienteId);
    List<InventarioItem> findByProveedorId(Long proveedorId);
    List<InventarioItem> findByActivoTrue();
    List<InventarioItem> findByStockActualLessThanEqual(Double stockMinimo);
}
