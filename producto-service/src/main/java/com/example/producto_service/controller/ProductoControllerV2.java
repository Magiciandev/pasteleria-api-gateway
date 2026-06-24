package com.example.producto_service.controller;

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

import com.example.producto_service.assemblers.ProductoModelAssembler;
import com.example.producto_service.model.Producto;
import com.example.producto_service.service.ProductoService;

@RestController
@RequestMapping("productos/v2")
public class ProductoControllerV2 {
    private final ProductoService productoService;
    private final ProductoModelAssembler assembler;
    private static final Logger logger = LoggerFactory.getLogger(ProductoControllerV2.class);

    public ProductoControllerV2(ProductoService productoService, ProductoModelAssembler assembler) {
        this.productoService = productoService;
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        logger.info("V2 GET /productos - Listando productos");
        List<EntityModel<Producto>> productos = productoService.listar().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(productos, linkTo(methodOn(ProductoControllerV2.class).listarProductos()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Producto> obtenerProducto(@PathVariable Long id) {
        logger.info("V2 GET /productos/{} - Obteniendo producto", id);
        Producto producto = productoService.buscarPorId(id);
        return assembler.toModel(producto);
    }
}