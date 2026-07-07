package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@Tag(name = "Carrito", description = "Operaciones para administrar productos agregados al carrito de compra")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @GetMapping
    @Operation(summary = "Listar carrito", description = "Obtiene todos los registros del carrito con usuario, producto y cantidad.")
    @ApiResponse(responseCode = "200", description = "Listado del carrito obtenido correctamente")
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener item del carrito por ID", description = "Busca un registro del carrito mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item del carrito encontrado",
                    content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado", content = @Content)
    })
    public ResponseEntity<Carrito> obtenerCarritoPorId(
            @Parameter(description = "Identificador del item del carrito", example = "1") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Agregar producto al carrito", description = "Registra un producto y su cantidad dentro del carrito de un usuario.")
    @ApiResponse(responseCode = "200", description = "Producto agregado al carrito correctamente",
            content = @Content(schema = @Schema(implementation = Carrito.class)))
    public Carrito agregarProductoAlCarrito(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del usuario, producto y cantidad a agregar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "usuario": { "id": 1 },
                                      "producto": { "id": 1 },
                                      "cantidad": 2
                                    }
                                    """)
                    )
            )
            @RequestBody Carrito carrito) {
        return carritoService.save(carrito);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar item del carrito", description = "Modifica la cantidad o referencias asociadas a un item del carrito.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item del carrito actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado", content = @Content)
    })
    public ResponseEntity<Carrito> actualizarCarrito(
            @Parameter(description = "Identificador del item del carrito", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del item del carrito",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "usuario": { "id": 1 },
                                      "producto": { "id": 1 },
                                      "cantidad": 3
                                    }
                                    """)
                    )
            )
            @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un item del carrito cuando existe.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Item del carrito eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Item del carrito no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "Identificador del item del carrito", example = "1") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
