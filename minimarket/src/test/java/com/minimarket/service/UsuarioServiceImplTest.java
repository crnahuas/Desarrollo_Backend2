package com.minimarket.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    @Test
    void findAllRetornaUsuariosDesdeRepositorio() {
        Usuario usuario = usuarioCompletoConRol("ADMIN");
        when(usuarioRepository.findAll()).thenReturn(java.util.List.of(usuario));

        java.util.List<Usuario> resultado = usuarioService.findAll();

        assertEquals(1, resultado.size());
        assertSame(usuario, resultado.get(0));
        verify(usuarioRepository).findAll();
    }

    @Test
    void saveDelegaPersistenciaEnRepositorio() {
        Usuario usuario = usuarioCompletoConRol("ADMIN");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario resultado = usuarioService.save(usuario);

        assertSame(usuario, resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deleteByIdDelegaEliminacionEnRepositorio() {
        usuarioService.deleteById(1L);

        verify(usuarioRepository).deleteById(1L);
    }

    @Test
    void findByUsernameRetornaUsuarioDesdeRepositorioMockeado() {
        Usuario usuario = usuarioCompletoConRol("ADMIN");
        when(usuarioRepository.findByUsername("adminUser")).thenReturn(Optional.of(usuario));

        Optional<Usuario> resultado = usuarioService.findByUsername("adminUser");

        assertTrue(resultado.isPresent());
        assertSame(usuario, resultado.get());
        verify(usuarioRepository).findByUsername("adminUser");
    }

    @Test
    void tieneDatosObligatoriosCompletosCuandoUsuarioPoseeInformacionRequerida() {
        Usuario usuario = usuarioCompletoConRol("VENDEDOR");

        boolean resultado = usuarioService.tieneDatosObligatoriosCompletos(usuario);

        assertTrue(resultado);
    }

    @Test
    void tieneDatosObligatoriosCompletosFallaCuandoEmailEstaVacio() {
        Usuario usuario = usuarioCompletoConRol("VENDEDOR");
        usuario.setEmail(" ");

        boolean resultado = usuarioService.tieneDatosObligatoriosCompletos(usuario);

        assertFalse(resultado);
    }

    @Test
    void puedeRegistrarVentasCuandoUsuarioTieneRolValido() {
        Usuario usuario = usuarioCompletoConRol("ADMIN");

        boolean resultado = usuarioService.puedeRegistrarVentas(usuario);

        assertTrue(resultado);
    }

    @Test
    void noPuedeRegistrarVentasCuandoUsuarioTieneRolNoValido() {
        Usuario usuario = usuarioCompletoConRol("CLIENTE");

        boolean resultado = usuarioService.puedeRegistrarVentas(usuario);

        assertFalse(resultado);
    }

    @Test
    void noPuedeRegistrarVentasCuandoUsuarioNoTieneRoles() {
        Usuario usuario = usuarioCompletoConRol("ADMIN");
        usuario.setRoles(Set.of());

        boolean resultado = usuarioService.puedeRegistrarVentas(usuario);

        assertFalse(resultado);
    }

    private Usuario usuarioCompletoConRol(String rol) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("adminUser");
        usuario.setPassword("securePassword123");
        usuario.setNombre("Ana");
        usuario.setApellido("Perez");
        usuario.setEmail("ana.perez@minimarket.cl");
        usuario.setDireccion("Los Aromos 123");
        usuario.setRoles(Set.of(new Rol(rol)));
        return usuario;
    }
}
