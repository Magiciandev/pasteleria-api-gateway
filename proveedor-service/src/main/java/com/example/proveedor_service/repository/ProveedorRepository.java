package com.example.proveedor_service.repository;

import com.example.proveedor_service.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByRut(String rut);

    List<Proveedor> findByActivo(Boolean activo);
}
