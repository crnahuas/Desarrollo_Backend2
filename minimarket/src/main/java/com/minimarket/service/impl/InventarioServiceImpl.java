package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    public Inventario save(Inventario inventario) {
        validarMovimiento(inventario);
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    private void validarMovimiento(Inventario inventario) {
        if (inventario == null) {
            throw new IllegalArgumentException("El movimiento de inventario es obligatorio");
        }
        if (inventario.getProducto() == null || inventario.getProducto().getId() == null) {
            throw new IllegalArgumentException("El producto asociado al inventario es obligatorio");
        }
        if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad del movimiento debe ser mayor a cero");
        }
        if (inventario.getTipoMovimiento() == null || inventario.getTipoMovimiento().isBlank()) {
            throw new IllegalArgumentException("El tipo de movimiento es obligatorio");
        }
        if (!inventario.getTipoMovimiento().equalsIgnoreCase("Entrada")
                && !inventario.getTipoMovimiento().equalsIgnoreCase("Salida")) {
            throw new IllegalArgumentException("El tipo de movimiento debe ser Entrada o Salida");
        }
        if (inventario.getFechaMovimiento() == null) {
            throw new IllegalArgumentException("La fecha del movimiento es obligatoria");
        }
    }
}
