package com.example.envio_service.controller;

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

import com.example.envio_service.assemblers.EnvioModelAssembler;
import com.example.envio_service.model.Envio;
import com.example.envio_service.service.EnvioService;

@RestController
@RequestMapping("envios/v2")
public class EnvioControllerV2 {
    private final EnvioService envioService;
    private final EnvioModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(EnvioControllerV2.class);

    public EnvioControllerV2(EnvioService envioService, EnvioModelAssembler assembler) {
        this.envioService = envioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Envio>> listarEnvios() {
        logger.info("V2 GET /envios - Listando envios");
        List<EntityModel<Envio>> envios = envioService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(envios, linkTo(methodOn(EnvioControllerV2.class).listarEnvios()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Envio> obtenerEnvio(@PathVariable Long id) {
        logger.info("V2 GET /envios/{} - Obteniendo envio", id);
        Envio envio = envioService.buscarPorId(id);
        return assembler.toModel(envio);
    }
}