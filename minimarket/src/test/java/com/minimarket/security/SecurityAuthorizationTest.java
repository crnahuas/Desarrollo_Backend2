package com.minimarket.security;

import com.minimarket.controller.InventarioController;
import com.minimarket.controller.ProductoController;
import com.minimarket.controller.UsuarioController;
import com.minimarket.controller.VentaController;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.security.config.SecurityConfig;
import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.service.InventarioService;
import com.minimarket.service.ProductoService;
import com.minimarket.service.UsuarioService;
import com.minimarket.service.VentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        ProductoController.class,
        InventarioController.class,
        VentaController.class,
        UsuarioController.class
})
@Import(SecurityConfig.class)
class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private InventarioService inventarioService;

    @MockBean
    private VentaService ventaService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminPuedeModificarProducto() throws Exception {
        Producto existente = producto();
        when(productoService.findById(1L)).thenReturn(existente);
        when(productoService.save(any(Producto.class))).thenReturn(existente);

        mockMvc.perform(put("/api/productos/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Arroz\",\"precio\":1800,\"stock\":20}"))
                .andExpect(status().isOk());

        verify(productoService).save(any(Producto.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clienteNoPuedeModificarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType("application/json")
                        .content("{\"nombre\":\"Arroz\",\"precio\":1800,\"stock\":20}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminPuedeRegistrarMovimientoDeInventario() throws Exception {
        Inventario movimiento = movimientoInventario();
        when(inventarioService.save(any(Inventario.class))).thenReturn(movimiento);

        mockMvc.perform(post("/api/inventario")
                        .contentType("application/json")
                        .content("{\"cantidad\":10,\"tipoMovimiento\":\"Entrada\",\"fechaMovimiento\":\"2026-06-29T12:00:00.000+00:00\"}"))
                .andExpect(status().isOk());

        verify(inventarioService).save(any(Inventario.class));
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cajeroNoPuedeRegistrarMovimientoDeInventario() throws Exception {
        mockMvc.perform(post("/api/inventario")
                        .contentType("application/json")
                        .content("{\"cantidad\":10,\"tipoMovimiento\":\"Entrada\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cajeroPuedeGenerarVenta() throws Exception {
        Venta venta = new Venta();
        when(ventaService.registrarVenta(any(Venta.class))).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                        .contentType("application/json")
                        .content("{\"fecha\":\"2026-06-29T12:00:00.000+00:00\"}"))
                .andExpect(status().isOk());

        verify(ventaService).registrarVenta(any(Venta.class));
    }

    @Test
    @WithMockUser(authorities = "CLIENTE")
    void clienteNoPuedeGenerarVenta() throws Exception {
        mockMvc.perform(post("/api/ventas")
                        .contentType("application/json")
                        .content("{\"fecha\":\"2026-06-29T12:00:00.000+00:00\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    void adminPuedeEliminarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService).deleteById(1L);
    }

    @Test
    @WithMockUser(authorities = "CAJERO")
    void cajeroNoPuedeEliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());
    }

    private Producto producto() {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setPrecio(1800.0);
        producto.setStock(20);
        return producto;
    }

    private Inventario movimientoInventario() {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        inventario.setCantidad(10);
        inventario.setTipoMovimiento("Entrada");
        inventario.setFechaMovimiento(new Date());
        return inventario;
    }
}
