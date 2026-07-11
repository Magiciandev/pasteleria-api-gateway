package com.example.compra_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.compra_service.controller.CompraControllerV2;
import com.example.compra_service.model.Compra;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class CompraModelAssembler implements RepresentationModelAssembler<Compra, EntityModel<Compra>> {

    @Override
    public EntityModel<Compra> toModel(Compra compra) {
        return EntityModel.of(compra,
                linkTo(methodOn(CompraControllerV2.class).obtenerCompra(compra.getId())).withSelfRel(),
                linkTo(methodOn(CompraControllerV2.class).listarCompras()).withRel("compras"));
    }
}
