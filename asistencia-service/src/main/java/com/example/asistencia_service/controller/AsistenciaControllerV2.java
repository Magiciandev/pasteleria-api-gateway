package com.example.asistencia_service.controller;

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

import com.example.asistencia_service.assemblers.AsistenciaModelAssembler;
import com.example.asistencia_service.model.Asistencia;
import com.example.asistencia_service.service.AsistenciaService;

@RestController
@RequestMapping("asistencias/v2")
public class AsistenciaControllerV2 {
    private final AsistenciaService asistenciaService;
    private final AsistenciaModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(AsistenciaControllerV2.class);

    public AsistenciaControllerV2(AsistenciaService asistenciaService, AsistenciaModelAssembler assembler) {
        this.asistenciaService = asistenciaService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Asistencia>> listarAsistencias() {
        logger.info("V2 GET /asistencias - Listando asistencias");
        List<EntityModel<Asistencia>> asistencias = asistenciaService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(asistencias, linkTo(methodOn(AsistenciaControllerV2.class).listarAsistencias()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Asistencia> obtenerAsistencia(@PathVariable Long id) {
        logger.info("V2 GET /asistencias/{} - Obteniendo asistencia", id);
        Asistencia asistencia = asistenciaService.buscarPorId(id);
        return assembler.toModel(asistencia);
    }
}