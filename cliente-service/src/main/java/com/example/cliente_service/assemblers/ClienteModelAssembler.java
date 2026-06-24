package com.example.cliente_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.cliente_service.controller.ClienteControllerV2;
import com.example.cliente_service.model.Cliente;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>> {

    @Override
    public EntityModel<Cliente> toModel(Cliente cliente) {
        return EntityModel.of(cliente,
                linkTo(methodOn(ClienteControllerV2.class).obtenerCliente(cliente.getId())).withSelfRel(),
                linkTo(methodOn(ClienteControllerV2.class).listarClientes()).withRel("clientes"));
            }
}