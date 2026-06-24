package com.example.pedido_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pedido_service.assemblers.PedidoModelAssembler;
import com.example.pedido_service.model.Pedido;
import com.example.pedido_service.service.PedidoService;

@RestController
@RequestMapping("pedidos/v2")
public class PedidoControllerV2 {
    private final PedidoService pedidoService;
    private final PedidoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(PedidoControllerV2.class);

    public PedidoControllerV2(PedidoService pedidoService, PedidoModelAssembler assembler) {
        this.pedidoService = pedidoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Pedido>> listarPedidos() {
        logger.info("V2 GET /pedidos - Listando pedidos");
        List<EntityModel<Pedido>> pedidos = pedidoService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(pedidos, linkTo(methodOn(PedidoControllerV2.class).listarPedidos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Pedido> obtenerPedido(@PathVariable Long id) {
        logger.info("V2 GET /pedidos/{} - Obteniendo pedido", id);
        Pedido pedido = pedidoService.buscarPorId(id);
        return assembler.toModel(pedido);
    }
}