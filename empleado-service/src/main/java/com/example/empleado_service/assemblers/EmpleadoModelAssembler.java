package com.example.empleado_service.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.empleado_service.controller.EmpleadoControllerV2;
import com.example.empleado_service.model.Empleado;

@Component
public class EmpleadoModelAssembler implements RepresentationModelAssembler<Empleado, EntityModel<Empleado>> {

    @Override
    public EntityModel<Empleado> toModel(Empleado empleado) {
        return EntityModel.of(empleado,
                linkTo(methodOn(EmpleadoControllerV2.class).obtenerEmpleado(empleado.getId())).withSelfRel(),
                linkTo(methodOn(EmpleadoControllerV2.class).listarEmpleados()).withRel("empleados"));
    }
}