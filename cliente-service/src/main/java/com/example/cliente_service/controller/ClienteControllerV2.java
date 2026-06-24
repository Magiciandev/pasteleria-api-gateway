package com.example.cliente_service.controller;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cliente_service.assemblers.ClienteModelAssembler;
import com.example.cliente_service.dto.ClienteDTO;
import com.example.cliente_service.model.Cliente;
import com.example.cliente_service.service.ClienteService;

@RestController
@RequestMapping("clientes/v2")
public class ClienteControllerV2 {
    private final ClienteService clienteService;
    private final ClienteModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ClienteControllerV2.class);

    public ClienteControllerV2(ClienteService clienteService, ClienteModelAssembler assembler) {
        this.clienteService = clienteService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Cliente>> listarClientes() {
        logger.info("V2 GET /clientes - Listando clientes");
        List<EntityModel<Cliente>> clientes = clienteService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(clientes, linkTo(methodOn(ClienteControllerV2.class).listarClientes()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Cliente> obtenerCliente(@PathVariable Long id) {
        logger.info("V2 GET /clientes/{} - Obteniendo cliente", id);
        Cliente cliente = clienteService.buscarPorId(id);
        return assembler.toModel(cliente);
    }
}