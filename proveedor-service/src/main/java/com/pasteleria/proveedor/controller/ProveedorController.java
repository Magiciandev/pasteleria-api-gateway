package com.pasteleria.proveedor.controller;

import com.pasteleria.proveedor.dto.ProveedorDTO;
import com.pasteleria.proveedor.service.ProveedorService;
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
@RequestMapping("/proveedores")
@Tag(name = "Proveedores", description = "Gestión de proveedores de la pastelería")
public class ProveedorController {
    
    @Autowired
    private ProveedorService proveedorService;

    @PostMapping
    @Operation(summary = "Crear proveedor", description = "Crea un nuevo proveedor en el sistema")
    @ApiResponse(responseCode = "201", description = "Proveedor creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    public ResponseEntity<ProveedorDTO> crear(
            @Parameter(description = "Datos del proveedor a crear") @RequestBody ProveedorDTO proveedorDTO) {
        ProveedorDTO creado = proveedorService.guardar(proveedorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    @Operation(summary = "Listar todos los proveedores", description = "Obtiene la lista completa de proveedores")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores obtenida")
    public ResponseEntity<List<ProveedorDTO>> listar() {
        List<ProveedorDTO> proveedores = proveedorService.listar();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/activos")
    @Operation(summary = "Listar proveedores activos", description = "Obtiene solo los proveedores activos")
    @ApiResponse(responseCode = "200", description = "Lista de proveedores activos")
    public ResponseEntity<List<ProveedorDTO>> listarActivos() {
        List<ProveedorDTO> proveedores = proveedorService.listarActivos();
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proveedor por ID", description = "Busca un proveedor específico por su identificador")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorDTO> buscarPorId(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        Optional<ProveedorDTO> proveedor = proveedorService.buscarPorId(id);
        return proveedor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/nombre/{nombre}")
    @Operation(summary = "Buscar proveedor por nombre", description = "Busca un proveedor por su nombre (sin importar mayúsculas/minúsculas)")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorDTO> buscarPorNombre(
            @Parameter(description = "Nombre del proveedor") @PathVariable String nombre) {
        Optional<ProveedorDTO> proveedor = proveedorService.buscarPorNombre(nombre);
        return proveedor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Buscar proveedor por email", description = "Busca un proveedor por su correo electrónico")
    @ApiResponse(responseCode = "200", description = "Proveedor encontrado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorDTO> buscarPorEmail(
            @Parameter(description = "Email del proveedor") @PathVariable String email) {
        Optional<ProveedorDTO> proveedor = proveedorService.buscarPorEmail(email);
        return proveedor.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proveedor", description = "Actualiza los datos de un proveedor existente")
    @ApiResponse(responseCode = "200", description = "Proveedor actualizado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<ProveedorDTO> actualizar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del proveedor") @RequestBody ProveedorDTO proveedorDTO) {
        ProveedorDTO actualizado = proveedorService.actualizar(id, proveedorDTO);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proveedor", description = "Elimina un proveedor del sistema")
    @ApiResponse(responseCode = "204", description = "Proveedor eliminado")
    @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        if (proveedorService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verificar existencia de proveedor", description = "Verifica si un proveedor existe por su ID")
    @ApiResponse(responseCode = "200", description = "Resultado de verificación")
    public ResponseEntity<Boolean> existe(
            @Parameter(description = "ID del proveedor") @PathVariable Long id) {
        return ResponseEntity.ok(proveedorService.existePorId(id));
    }
}
