package com.example.inventario_service.controller;

import com.example.inventario_service.assemblers.MovimientoInventarioModelAssembler;
import com.example.inventario_service.model.MovimientoInventario;
import com.example.inventario_service.service.MovimientoInventarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("inventario/v2")
public class MovimientoInventarioControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioControllerV2.class);
    private final MovimientoInventarioService inventarioService;
    private final MovimientoInventarioModelAssembler assembler;

    public MovimientoInventarioControllerV2(MovimientoInventarioService inventarioService,
                                             MovimientoInventarioModelAssembler assembler) {
        this.inventarioService = inventarioService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<MovimientoInventario>> listarMovimientos() {
        logger.info("V2 GET /inventario - Listando movimientos de inventario");

        List<EntityModel<MovimientoInventario>> movimientos = inventarioService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(movimientos,
                linkTo(methodOn(MovimientoInventarioControllerV2.class).listarMovimientos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<MovimientoInventario> obtenerMovimiento(@PathVariable Long id) {
        logger.info("V2 GET /inventario/{} - Obteniendo movimiento de inventario", id);
        MovimientoInventario movimiento = inventarioService.buscarPorId(id);
        return assembler.toModel(movimiento);
    }
}
