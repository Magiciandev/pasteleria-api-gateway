package com.example.cliente_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.cliente_service.model.Cliente;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Búsqueda por atributo distinto al ID (Punto 7)
    Optional<Cliente> findByRutCliente(String rutCliente);
    
    // Búsqueda por correo
    Optional<Cliente> findByCorreo(String correo);
    
    // Búsqueda por nombre (contiene)
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
}