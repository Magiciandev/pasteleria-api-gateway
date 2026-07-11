package com.example.inventario_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.inventario_service.controller.MovimientoInventarioControllerV2;
import com.example.inventario_service.model.MovimientoInventario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class MovimientoInventarioModelAssembler
        implements RepresentationModelAssembler<MovimientoInventario, EntityModel<MovimientoInventario>> {

    @Override
    public EntityModel<MovimientoInventario> toModel(MovimientoInventario movimiento) {
        return EntityModel.of(movimiento,
                linkTo(methodOn(MovimientoInventarioControllerV2.class).obtenerMovimiento(movimiento.getId())).withSelfRel(),
                linkTo(methodOn(MovimientoInventarioControllerV2.class).listarMovimientos()).withRel("movimientos"));
    }
}
