package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    void saveRegistraMovimientoCuandoDatosSonValidos() {
        Inventario inventario = inventario(producto(20L, "Cafe"), "Entrada", 12);
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        Inventario resultado = inventarioService.save(inventario);

        assertSame(inventario, resultado);
        assertSame(inventario.getProducto(), resultado.getProducto());
        assertEquals("Entrada", resultado.getTipoMovimiento());
        assertEquals(12, resultado.getCantidad());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void saveNoGuardaCuandoTipoMovimientoEstaVacio() {
        Inventario inventario = inventario(producto(20L, "Cafe"), " ", 8);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.save(inventario)
        );

        assertEquals("El tipo de movimiento es obligatorio", error.getMessage());
        verify(inventarioRepository, never()).save(inventario);
    }

    @Test
    void saveNoGuardaCuandoCantidadEsNula() {
        Inventario inventario = inventario(producto(20L, "Cafe"), "Salida", null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.save(inventario)
        );

        assertEquals("La cantidad del movimiento debe ser mayor a cero", error.getMessage());
        verify(inventarioRepository, never()).save(inventario);
    }

    @Test
    void saveNoGuardaCuandoTipoMovimientoNoEsEntradaNiSalida() {
        Inventario inventario = inventario(producto(20L, "Cafe"), "Ajuste", 5);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.save(inventario)
        );

        assertEquals("El tipo de movimiento debe ser Entrada o Salida", error.getMessage());
        verify(inventarioRepository, never()).save(inventario);
    }

    @Test
    void findByProductoIdRetornaMovimientosDelProductoCorrecto() {
        Inventario inventario = inventario(producto(20L, "Cafe"), "Entrada", 10);
        when(inventarioRepository.findByProductoId(20L)).thenReturn(List.of(inventario));

        List<Inventario> resultado = inventarioService.findByProductoId(20L);

        assertEquals(1, resultado.size());
        assertSame(inventario, resultado.get(0));
        assertEquals(20L, resultado.get(0).getProducto().getId());
        verify(inventarioRepository).findByProductoId(20L);
    }

    @Test
    void findByIdRetornaMovimientoCuandoExiste() {
        Inventario inventario = inventario(producto(20L, "Cafe"), "Salida", 3);
        when(inventarioRepository.findById(9L)).thenReturn(Optional.of(inventario));

        Inventario resultado = inventarioService.findById(9L);

        assertSame(inventario, resultado);
        verify(inventarioRepository).findById(9L);
    }

    @Test
    void deleteByIdDelegaEliminacionEnRepositorio() {
        inventarioService.deleteById(6L);

        verify(inventarioRepository).deleteById(6L);
    }

    private Inventario inventario(Producto producto, String tipoMovimiento, Integer cantidad) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setTipoMovimiento(tipoMovimiento);
        inventario.setCantidad(cantidad);
        inventario.setFechaMovimiento(new Date());
        return inventario;
    }

    private Producto producto(Long id, String nombre) {
        Producto producto = new Producto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecio(2500.0);
        producto.setStock(15);
        return producto;
    }
}
