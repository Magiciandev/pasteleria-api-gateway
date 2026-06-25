package com.example.proveedor_service.controller;

import com.example.proveedor_service.assemblers.ProveedorModelAssembler;
import com.example.proveedor_service.model.Proveedor;
import com.example.proveedor_service.service.ProveedorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("proveedores/v2")
public class ProveedorControllerV2 {

    private final ProveedorService proveedorService;
    private final ProveedorModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ProveedorControllerV2.class);

    public ProveedorControllerV2(ProveedorService proveedorService, ProveedorModelAssembler assembler) {
        this.proveedorService = proveedorService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Proveedor>> listarProveedores() {
        logger.info("V2 GET /proveedores - Listando proveedores");
        
        List<EntityModel<Proveedor>> proveedores = proveedorService.listar().stream()
                .map(proveedor -> assembler.toModel(proveedor))
                .collect(Collectors.toList());
                
        return CollectionModel.of(proveedores,
                linkTo(methodOn(ProveedorControllerV2.class).listarProveedores()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Proveedor> obtenerProveedor(@PathVariable Long id) {
        logger.info("V2 GET /proveedores/{} - Obteniendo proveedor", id);
        Proveedor proveedor = proveedorService.buscarPorId(id);
        return assembler.toModel(proveedor);
    }
}