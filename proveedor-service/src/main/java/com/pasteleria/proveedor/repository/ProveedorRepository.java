package com.pasteleria.proveedor.repository;

import com.pasteleria.proveedor.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    Optional<Proveedor> findByNombreIgnoreCase(String nombre);
    Optional<Proveedor> findByEmailIgnoreCase(String email);
    List<Proveedor> findByActivoTrue();
}
