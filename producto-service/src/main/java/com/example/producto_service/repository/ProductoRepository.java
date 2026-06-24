package com.example.producto_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.producto_service.model.Producto;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Búsqueda por atributo distinto al ID (Punto 7, opcional aquí pero suma puntos)
    Optional<Producto> findByNombre(String nombre);
}