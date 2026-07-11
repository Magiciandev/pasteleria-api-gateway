package com.example.compra_service.repository;

import com.example.compra_service.model.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    List<Compra> findByIdProveedor(Long idProveedor);

    List<Compra> findByEstado(String estado);

    List<Compra> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
