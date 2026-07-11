package com.example.inventario_service.repository;

import com.example.inventario_service.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    List<MovimientoInventario> findByIdProducto(Long idProducto);

    List<MovimientoInventario> findByTipo(String tipo);

    List<MovimientoInventario> findByIdProductoOrderByFechaDesc(Long idProducto);
}
