package com.example.pedido_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.pedido_service.controller.PedidoControllerV2;
import com.example.pedido_service.model.Pedido;

@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        return EntityModel.of(pedido,
                linkTo(methodOn(PedidoControllerV2.class).obtenerPedido(pedido.getId())).withSelfRel(),
                linkTo(methodOn(PedidoControllerV2.class).listarPedidos()).withRel("pedidos"));
    }
}