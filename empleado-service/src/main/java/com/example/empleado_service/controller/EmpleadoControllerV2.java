package com.example.empleado_service.controller;

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

import com.example.empleado_service.assemblers.EmpleadoModelAssembler;
import com.example.empleado_service.model.Empleado;
import com.example.empleado_service.service.EmpleadoService;

@RestController
@RequestMapping("empleados/v2")
public class EmpleadoControllerV2 {
    private final EmpleadoService empleadoService;
    private final EmpleadoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(EmpleadoControllerV2.class);

    public EmpleadoControllerV2(EmpleadoService empleadoService, EmpleadoModelAssembler assembler) {
        this.empleadoService = empleadoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Empleado>> listarEmpleados() {
        logger.info("V2 GET /empleados - Listando empleados");
        List<EntityModel<Empleado>> empleados = empleadoService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(empleados, linkTo(methodOn(EmpleadoControllerV2.class).listarEmpleados()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Empleado> obtenerEmpleado(@PathVariable Long id) {
        logger.info("V2 GET /empleados/{} - Obteniendo empleado", id);
        Empleado empleado = empleadoService.buscarPorId(id);
        return assembler.toModel(empleado);
    }
}