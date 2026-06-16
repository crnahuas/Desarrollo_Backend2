package com.minimarket.service;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    void findAllRetornaVentasDesdeRepositorio() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Leche", 1200, 10), 1, 1200.0);
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findAll();

        assertEquals(1, resultado.size());
        assertSame(venta, resultado.get(0));
        verify(ventaRepository).findAll();
    }

    @Test
    void findByIdRetornaVentaCuandoExiste() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Leche", 1200, 10), 1, 1200.0);
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(venta));

        Venta resultado = ventaService.findById(10L);

        assertSame(venta, resultado);
        verify(ventaRepository).findById(10L);
    }

    @Test
    void saveDelegaPersistenciaEnRepositorio() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Leche", 1200, 10), 1, 1200.0);
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.save(venta);

        assertSame(venta, resultado);
        verify(ventaRepository).save(venta);
    }

    @Test
    void findByUsuarioIdRetornaVentasAsociadasAlUsuario() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Leche", 1200, 10), 1, 1200.0);
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(venta));

        List<Venta> resultado = ventaService.findByUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertSame(venta, resultado.get(0));
        verify(ventaRepository).findByUsuarioId(1L);
    }

    @Test
    void registrarVentaGuardaCuandoUsuarioExisteYHayStockSuficiente() {
        Usuario usuarioPersistido = usuario(1L);
        Venta venta = ventaConDetalle(usuario(1L), producto("Leche", 1200, 10), 3, 1200.0);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPersistido));
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.registrarVenta(venta);

        assertSame(venta, resultado);
        assertSame(usuarioPersistido, resultado.getUsuario());
        verify(usuarioRepository).findById(1L);
        verify(ventaRepository).save(venta);
    }

    @Test
    void registrarVentaNoGuardaCuandoNoHayStockSuficiente() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Arroz", 1800, 2), 5, 1800.0);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario(1L)));

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertEquals("No hay stock suficiente para registrar la venta", error.getMessage());
        verify(ventaRepository, never()).save(venta);
    }

    @Test
    void registrarVentaNoGuardaCuandoUsuarioNoExiste() {
        Venta venta = ventaConDetalle(usuario(99L), producto("Pan", 1500, 8), 2, 1500.0);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.registrarVenta(venta)
        );

        assertEquals("El usuario asociado a la venta no existe", error.getMessage());
        verify(ventaRepository, never()).save(venta);
    }

    @Test
    void tieneStockSuficienteValidaTodosLosProductosDeLaVenta() {
        Venta venta = ventaConDetalles(
                usuario(1L),
                detalle(producto("Leche", 1200, 10), 2, 1200.0),
                detalle(producto("Pan", 1500, 5), 5, 1500.0)
        );

        boolean resultado = ventaService.tieneStockSuficiente(venta);

        assertTrue(resultado);
    }

    @Test
    void tieneStockSuficienteRetornaFalsoSiUnProductoNoAlcanza() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Cafe", 4500, 1), 2, 4500.0);

        boolean resultado = ventaService.tieneStockSuficiente(venta);

        assertFalse(resultado);
    }

    @Test
    void calcularTotalSumaPrecioPorCantidadDeCadaDetalle() {
        Venta venta = ventaConDetalles(
                usuario(1L),
                detalle(producto("Leche", 1200, 10), 2, 1200.0),
                detalle(producto("Pan", 1500, 5), 3, 1500.0)
        );

        double total = ventaService.calcularTotal(venta);

        assertEquals(6900, total);
    }

    @Test
    void calcularTotalUsaPrecioDelProductoCuandoDetalleNoTienePrecio() {
        Venta venta = ventaConDetalle(usuario(1L), producto("Jugo", 990, 7), 4, null);

        double total = ventaService.calcularTotal(venta);

        assertEquals(3960, total);
    }

    @Test
    void calcularTotalRetornaCeroCuandoVentaNoTieneDetalles() {
        Venta venta = new Venta();

        double total = ventaService.calcularTotal(venta);

        assertEquals(0, total);
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("vendedor");
        usuario.setPassword("clave");
        usuario.setNombre("Carlos");
        usuario.setApellido("Soto");
        usuario.setEmail("carlos.soto@minimarket.cl");
        usuario.setDireccion("Av. Norte 456");
        return usuario;
    }

    private Producto producto(String nombre, double precio, int stock) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        return producto;
    }

    private Venta ventaConDetalle(Usuario usuario, Producto producto, int cantidad, Double precio) {
        return ventaConDetalles(usuario, detalle(producto, cantidad, precio));
    }

    private Venta ventaConDetalles(Usuario usuario, DetalleVenta... detalles) {
        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        venta.setDetalles(List.of(detalles));
        return venta;
    }

    private DetalleVenta detalle(Producto producto, int cantidad, Double precio) {
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        return detalle;
    }
}
