package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito save(Carrito carrito) {
        validarCarrito(carrito);
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> findByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    private void validarCarrito(Carrito carrito) {
        if (carrito == null) {
            throw new IllegalArgumentException("El carrito es obligatorio");
        }
        if (carrito.getUsuario() == null || carrito.getUsuario().getId() == null) {
            throw new IllegalArgumentException("El usuario asociado al carrito es obligatorio");
        }
        if (carrito.getProducto() == null || carrito.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto asociado al carrito es obligatorio");
        }
        if (carrito.getCantidad() == null || carrito.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del carrito debe ser mayor a cero");
        }
        if (carrito.getProducto().getStock() == null || carrito.getProducto().getStock() < carrito.getCantidad()) {
            throw new IllegalArgumentException("No hay stock suficiente para agregar el producto al carrito");
        }
    }
}
