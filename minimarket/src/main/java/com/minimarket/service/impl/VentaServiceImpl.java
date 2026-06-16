package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Venta registrarVenta(Venta venta) {
        if (venta == null || venta.getUsuario() == null || venta.getUsuario().getId() == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario valido");
        }

        Optional<Usuario> usuario = usuarioRepository.findById(venta.getUsuario().getId());
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("El usuario asociado a la venta no existe");
        }

        if (!tieneStockSuficiente(venta)) {
            throw new IllegalArgumentException("No hay stock suficiente para registrar la venta");
        }

        venta.setUsuario(usuario.get());
        return ventaRepository.save(venta);
    }

    @Override
    public boolean tieneStockSuficiente(Venta venta) {
        if (venta == null || venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            return false;
        }

        return venta.getDetalles().stream().allMatch(detalle ->
                detalle.getProducto() != null
                        && detalle.getProducto().getStock() != null
                        && detalle.getCantidad() != null
                        && detalle.getCantidad() > 0
                        && detalle.getProducto().getStock() >= detalle.getCantidad());
    }

    @Override
    public double calcularTotal(Venta venta) {
        if (venta == null || venta.getDetalles() == null) {
            return 0;
        }

        return venta.getDetalles().stream()
                .mapToDouble(this::calcularSubtotal)
                .sum();
    }

    private double calcularSubtotal(DetalleVenta detalle) {
        if (detalle == null || detalle.getCantidad() == null) {
            return 0;
        }

        double precio = detalle.getPrecio() != null
                ? detalle.getPrecio()
                : detalle.getProducto() != null && detalle.getProducto().getPrecio() != null
                ? detalle.getProducto().getPrecio()
                : 0;

        return precio * detalle.getCantidad();
    }
}
