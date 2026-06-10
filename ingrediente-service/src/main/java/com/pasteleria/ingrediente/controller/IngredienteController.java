package com.pasteleria.ingrediente.controller;

import com.pasteleria.ingrediente.dto.IngredienteDTO;
import com.pasteleria.ingrediente.service.IngredienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredientes")
@Tag(name = "Ingredientes", description = "Gestión de ingredientes de la pastelería")
public class IngredienteController {
    
    @Autowired
    private IngredienteService ingredienteService;

    @PostMapping
    @Operation(summary = "Crear ingrediente", description = "Crea un nuevo ingrediente en el sistema")
    @ApiResponse(responseCode = "201", description = "Ingrediente creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<IngredienteDTO> crear(
            @Parameter(description = "Datos del ingrediente a crear") @RequestBody IngredienteDTO ingredienteDTO) {
        IngredienteDTO creado = ingredienteService.guardar(ingredienteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar todos los ingredientes", description = "Obtiene la lista completa de ingredientes")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes obtenida")
    public ResponseEntity<List<IngredienteDTO>> listar() {
        List<IngredienteDTO> ingredientes = ingredienteService.listar();
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar ingredientes activos", description = "Obtiene solo los ingredientes activos")
    @ApiResponse(responseCode = "200", description = "Lista de ingredientes activos")
    public ResponseEntity<List<IngredienteDTO>> listarActivos() {
        List<IngredienteDTO> ingredientes = ingredienteService.listarActivos();
        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ingrediente por ID", description = "Busca un ingrediente específico por su identificador")
    @ApiResponse(responseCode = "200", description = "Ingrediente encontrado")
    @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    public ResponseEntity<IngredienteDTO> buscarPorId(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id) {
        Optional<IngredienteDTO> ingrediente = ingredienteService.buscarPorId(id);
        return ingrediente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Buscar ingrediente por nombre", description = "Busca un ingrediente por su nombre (sin importar mayúsculas/minúsculas)")
    @ApiResponse(responseCode = "200", description = "Ingrediente encontrado")
    @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    public ResponseEntity<IngredienteDTO> buscarPorNombre(
            @Parameter(description = "Nombre del ingrediente") @PathVariable String nombre) {
        Optional<IngredienteDTO> ingrediente = ingredienteService.buscarPorNombre(nombre);
        return ingrediente.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar ingrediente", description = "Actualiza los datos de un ingrediente existente")
    @ApiResponse(responseCode = "200", description = "Ingrediente actualizado")
    @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    public ResponseEntity<IngredienteDTO> actualizar(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del ingrediente") @RequestBody IngredienteDTO ingredienteDTO) {
        IngredienteDTO actualizado = ingredienteService.actualizar(id, ingredienteDTO);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar ingrediente", description = "Elimina un ingrediente del sistema")
    @ApiResponse(responseCode = "204", description = "Ingrediente eliminado")
    @ApiResponse(responseCode = "404", description = "Ingrediente no encontrado")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id) {
        if (ingredienteService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verificar existencia de ingrediente", description = "Verifica si un ingrediente existe por su ID")
    @ApiResponse(responseCode = "200", description = "Resultado de verificación")
    public ResponseEntity<Boolean> existe(
            @Parameter(description = "ID del ingrediente") @PathVariable Long id) {
        return ResponseEntity.ok(ingredienteService.existePorId(id));
    }
}
