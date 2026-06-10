package com.example.producto_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.producto_service.model.DetalleProducto;

@Repository
public interface DetalleProductoRepository extends JpaRepository<DetalleProducto, Long> {
}
