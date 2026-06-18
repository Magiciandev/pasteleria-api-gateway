package com.example.envio_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.envio_service.model.Envio;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, Long> {
    
    /**
     * Buscar envíos por cliente
     */
    List<Envio> findByIdCliente(Long idCliente);
    
    /**
     * Buscar envío por pedido
     */
    Optional<Envio> findByIdPedido(Long idPedido);
    
    /**
     * Buscar envíos por estado
     */
    List<Envio> findByEstado(String estado);
}
