package com.example.asistencia_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.asistencia_service.controller.AsistenciaControllerV2;
import com.example.asistencia_service.model.Asistencia;

@Component
public class AsistenciaModelAssembler implements RepresentationModelAssembler<Asistencia, EntityModel<Asistencia>> {

    @Override
    public EntityModel<Asistencia> toModel(Asistencia asistencia) {
        return EntityModel.of(asistencia,
                linkTo(methodOn(AsistenciaControllerV2.class).obtenerAsistencia(asistencia.getId())).withSelfRel(),
                linkTo(methodOn(AsistenciaControllerV2.class).listarAsistencias()).withRel("asistencias"));
    }
}