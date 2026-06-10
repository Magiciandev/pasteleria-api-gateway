package com.example.pedido_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.pedido_service.model.Pedido;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Búsqueda por atributo distinto al ID (Punto 7: Buscar las compras de un cliente)
    List<Pedido> findByIdCliente(Long idCliente);
    
    // Búsqueda por rango de fechas
    List<Pedido> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}