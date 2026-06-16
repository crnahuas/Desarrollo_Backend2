package com.minimarket.service.impl;

import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Set<String> ROLES_HABILITADOS_PARA_VENTA = Set.of("ADMIN", "VENDEDOR");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean tieneDatosObligatoriosCompletos(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        return tieneTexto(usuario.getNombre())
                && tieneTexto(usuario.getApellido())
                && tieneTexto(usuario.getEmail())
                && tieneTexto(usuario.getDireccion());
    }

    @Override
    public boolean puedeRegistrarVentas(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null || usuario.getRoles().isEmpty()) {
            return false;
        }

        return usuario.getRoles().stream()
                .anyMatch(rol -> rol.getNombre() != null
                        && ROLES_HABILITADOS_PARA_VENTA.contains(rol.getNombre().toUpperCase()));
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
