package com.example.compra_service.controller;

import com.example.compra_service.assemblers.CompraModelAssembler;
import com.example.compra_service.model.Compra;
import com.example.compra_service.service.CompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("compras/v2")
public class CompraControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(CompraControllerV2.class);
    private final CompraService compraService;
    private final CompraModelAssembler assembler;

    public CompraControllerV2(CompraService compraService, CompraModelAssembler assembler) {
        this.compraService = compraService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Compra>> listarCompras() {
        logger.info("V2 GET /compras - Listando compras");

        List<EntityModel<Compra>> compras = compraService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(compras,
                linkTo(methodOn(CompraControllerV2.class).listarCompras()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Compra> obtenerCompra(@PathVariable Long id) {
        logger.info("V2 GET /compras/{} - Obteniendo compra", id);
        Compra compra = compraService.buscarPorId(id);
        return assembler.toModel(compra);
    }
}
