package com.example.proveedor_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.example.proveedor_service.controller.ProveedorControllerV2;
import com.example.proveedor_service.model.Proveedor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class ProveedorModelAssembler implements RepresentationModelAssembler<Proveedor, EntityModel<Proveedor>> {

    @Override
    public EntityModel<Proveedor> toModel(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(ProveedorControllerV2.class).obtenerProveedor(proveedor.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorControllerV2.class).listarProveedores()).withRel("proveedores"));
    }
}
