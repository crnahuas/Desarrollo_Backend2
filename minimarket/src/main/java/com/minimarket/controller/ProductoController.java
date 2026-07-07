package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Operaciones para consultar y mantener productos del minimarket")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtiene todos los productos registrados con su precio, stock y categoria asociada.")
    @ApiResponse(responseCode = "200", description = "Listado de productos obtenido correctamente")
    public List<Producto> listarProductos() {
        return productoService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Busca un producto especifico mediante su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Producto> obtenerProductoPorId(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        return (producto != null) ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Crear producto", description = "Registra un nuevo producto asociado a una categoria existente.")
    @ApiResponse(responseCode = "200", description = "Producto creado correctamente",
            content = @Content(schema = @Schema(implementation = Producto.class)))
    public Producto guardarProducto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Arroz grano largo",
                                      "precio": 1290,
                                      "stock": 50,
                                      "categoria": { "id": 1 }
                                    }
                                    """)
                    )
            )
            @RequestBody Producto producto) {
        return productoService.save(producto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente",
                    content = @Content(schema = @Schema(implementation = Producto.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Producto> actualizarProducto(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del producto",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "nombre": "Arroz premium",
                                      "precio": 1490,
                                      "stock": 60,
                                      "categoria": { "id": 1 }
                                    }
                                    """)
                    )
            )
            @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            producto.setId(id);
            return ResponseEntity.ok(productoService.save(producto));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto registrado cuando existe en el sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "Identificador del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
