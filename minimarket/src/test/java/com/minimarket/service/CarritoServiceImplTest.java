package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarritoServiceImplTest {

    @Mock
    private CarritoRepository carritoRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    @Test
    void findAllRetornaCarritosDesdeRepositorio() {
        Carrito carrito = carrito(usuario(1L), producto(10L, "Arroz", 8), 3);
        when(carritoRepository.findAll()).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findAll();

        assertEquals(1, resultado.size());
        assertSame(carrito, resultado.get(0));
        verify(carritoRepository).findAll();
    }

    @Test
    void savePermiteAgregarProductoCuandoExisteStockSuficiente() {
        Carrito carrito = carrito(usuario(1L), producto(10L, "Arroz", 8), 3);
        when(carritoRepository.save(carrito)).thenReturn(carrito);

        Carrito resultado = carritoService.save(carrito);

        assertSame(carrito, resultado);
        assertSame(carrito.getUsuario(), resultado.getUsuario());
        assertSame(carrito.getProducto(), resultado.getProducto());
        verify(carritoRepository).save(carrito);
    }

    @Test
    void saveNoGuardaProductoCuandoNoExisteStockSuficiente() {
        Carrito carrito = carrito(usuario(1L), producto(10L, "Aceite", 2), 5);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.save(carrito)
        );

        assertEquals("No hay stock suficiente para agregar el producto al carrito", error.getMessage());
        verify(carritoRepository, never()).save(carrito);
    }

    @Test
    void saveNoGuardaCuandoUsuarioNoEstaAsociado() {
        Carrito carrito = carrito(null, producto(10L, "Azucar", 4), 1);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.save(carrito)
        );

        assertEquals("El usuario asociado al carrito es obligatorio", error.getMessage());
        verify(carritoRepository, never()).save(carrito);
    }

    @Test
    void findByUsuarioIdRetornaCarritosAsociadosAlUsuario() {
        Carrito carrito = carrito(usuario(1L), producto(10L, "Fideos", 6), 2);
        when(carritoRepository.findByUsuarioId(1L)).thenReturn(List.of(carrito));

        List<Carrito> resultado = carritoService.findByUsuarioId(1L);

        assertEquals(1, resultado.size());
        assertSame(carrito, resultado.get(0));
        assertEquals(1L, resultado.get(0).getUsuario().getId());
        verify(carritoRepository).findByUsuarioId(1L);
    }

    @Test
    void findByIdRetornaCarritoCuandoExiste() {
        Carrito carrito = carrito(usuario(1L), producto(10L, "Leche", 9), 1);
        when(carritoRepository.findById(7L)).thenReturn(Optional.of(carrito));

        Carrito resultado = carritoService.findById(7L);

        assertSame(carrito, resultado);
        verify(carritoRepository).findById(7L);
    }

    @Test
    void deleteByIdDelegaEliminacionEnRepositorio() {
        carritoService.deleteById(4L);

        verify(carritoRepository).deleteById(4L);
    }

    private Carrito carrito(Usuario usuario, Producto producto, int cantidad) {
        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(cantidad);
        return carrito;
    }

    private Usuario usuario(Long id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setUsername("cliente");
        usuario.setNombre("Cliente");
        usuario.setApellido("Prueba");
        usuario.setEmail("cliente@minimarket.cl");
        usuario.setDireccion("Av. Central 123");
        return usuario;
    }

    private Producto producto(Long id, String nombre, int stock) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(1200.0);
        producto.setStock(stock);
        return producto;
    }
}
