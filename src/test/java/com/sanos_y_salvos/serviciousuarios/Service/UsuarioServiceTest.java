package com.sanos_y_salvos.serviciousuarios.Service;


import com.sanos_y_salvos.serviciousuarios.DTO.PasswordChangeDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.UserProfileDTO;
import com.sanos_y_salvos.serviciousuarios.Model.Rol;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setName("Jeffrey Epstein");
        usuarioPrueba.setEmail("Jeffrey16@gmail.com");
        usuarioPrueba.setRol(Rol.ROL_DUENO);
        usuarioPrueba.setActivo(true);
    }

    @Test
    void buscarPorEmail_DeberiaRetornarUsuario() {
        when(usuarioRepository.findByEmail("Jeffrey16@gmail.com")).thenReturn(java.util.Optional.of(usuarioPrueba));

        Usuario resultado = usuarioService.findByEmail("Jeffrey16@gmail.com");

        assertNotNull(resultado, "El usuario no debería ser nulo");
        assertEquals("Jeffrey Epstein", resultado.getName(), "El nombre no coincide");
        assertEquals("Jeffrey16@gmail.com", resultado.getEmail());

        verify(usuarioRepository, times(1)).findByEmail("Jeffrey16@gmail.com");
    }


    @Test
    void registrar_CuandoPasswordEsNulo_DeberiaLanzarIllegalArgument() {
        Usuario usuarioSinPassword = new Usuario();
        usuarioSinPassword.setEmail("Jeffrey16@gmail.com");
        usuarioSinPassword.setName("Jeffrey");
        usuarioSinPassword.setPassword(null);


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.save(usuarioSinPassword);
        });

        assertEquals("La contraseña no puede estar vacía...", exception.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, never()).save(any(Usuario.class));

    }

    @Test
    void guardar_UsuarioValido_DeberiaEncriptarYGuardar() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmail("nuevocorreo@gmail.com");
        nuevoUsuario.setPassword("12345");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setEmail("nuevocorreo@gmail.com");
        usuarioGuardado.setPassword("hash_encriptado");

        when(passwordEncoder.encode("12345")).thenReturn("hash_encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.save(nuevoUsuario);

        assertNotNull(resultado);
        assertEquals("hash_encriptado", resultado.getPassword(), "La contraseña debería estar encriptada");

        verify(passwordEncoder, times(1)).encode("12345");
        verify(usuarioRepository, times(1)).save(nuevoUsuario);
    }

    @Test
    void changeUserPassword_CuandoPasswordActualEsIncorrecta_DeberiaLanzarExcepcion() {
        usuarioPrueba.setPassword("hash_real_de_la_bd");
        PasswordChangeDTO dto = new PasswordChangeDTO("clave_equivocada", "nueva_clave");

        when(usuarioRepository.findByEmail("Jeffrey16@gmail.com")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("clave_equivocada", "hash_real_de_la_bd")).thenReturn(false);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.changeUserPassword("Jeffrey16@gmail.com", dto);
        });

        assertEquals("La contraseña actual es incorrecta...", excepcion.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void findByEmail_CuandoUsuarioNoExiste_DeberiaLanzarNoSuchElementException() {
        when(usuarioRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            usuarioService.findByEmail("noexiste@gmail.com");
        });

        assertEquals("Usuario no encontrado o Desactivado", excepcion.getMessage());
    }

    @Test
    void deleteByEmail_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(usuarioRepository.existsByEmail("noexiste@gmail.com")).thenReturn(false);

        NoSuchElementException excepcion = assertThrows(NoSuchElementException.class, () -> {
            usuarioService.deleteByEmail("noexiste@gmail.com");
        });

        assertEquals("Usuario no encontrado", excepcion.getMessage());
        verify(usuarioRepository, never()).deleteByEmail(anyString());
    }

    @Test
    void loadUserByUsername_CuandoNoExiste_DeberiaLanzarUsernameNotFoundException() {
        when(usuarioRepository.findByEmail("noexiste@gmail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException excepcion = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.loadUserByUsername("noexiste@gmail.com");
        });

        assertEquals("Usuario no encontrado", excepcion.getMessage());
    }

    @Test
    void loadUserByUsername_CuandoEstaDesactivado_DeberiaLanzarUsernameNotFoundException() {
        Usuario inactivo = new Usuario();
        inactivo.setEmail("inactivo@gmail.com");
        inactivo.setActivo(false);
        when(usuarioRepository.findByEmail("inactivo@gmail.com")).thenReturn(Optional.of(inactivo));

        UsernameNotFoundException excepcion = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.loadUserByUsername("inactivo@gmail.com");
        });

        assertEquals("Cuenta Desactivada", excepcion.getMessage());
    }

    @Test
    void updateProfile_CuandoNombreEsNuloOVacio_NoDeberiaCambiarElNombre() {
        when(usuarioRepository.findByEmail("Jeffrey16@gmail.com")).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        UserProfileDTO dtoTrampa = new UserProfileDTO();
        dtoTrampa.setName("   ");
        dtoTrampa.setAddress(null);

        UserProfileDTO resultado = usuarioService.updateProfile("Jeffrey16@gmail.com", dtoTrampa);


        assertEquals("Jeffrey Epstein", resultado.getName());
        assertNull(resultado.getAddress());
    }

    @Test
    void existsByEmail_DeberiaRetornarLoQueDigaElRepo() {
        when(usuarioRepository.existsByEmail("existe@gmail.com")).thenReturn(true);
        assertTrue(usuarioService.existsByEmail("existe@gmail.com"));

        when(usuarioRepository.existsByEmail("no@gmail.com")).thenReturn(false);
        assertFalse(usuarioService.existsByEmail("no@gmail.com"));
    }

    @Test
    void deactivateUserByEmail_DeberiaCambiarEstadoAFalse() {
        when(usuarioRepository.findByEmail("Jeffrey16@gmail.com")).thenReturn(Optional.of(usuarioPrueba));

        usuarioService.deactivateUserByEmail("Jeffrey16@gmail.com");

        assertFalse(usuarioPrueba.isActivo(), "El usuario debería quedar inactivo");
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }

    @Test
    void adminResetPassword_DeberiaEncriptarYGuardarNuevaClave() {

        when(usuarioRepository.findByEmail("Jeffrey16@gmail.com")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.encode("nueva_clave_secreta")).thenReturn("hash_nuevo");

        usuarioService.adminResetPassword("Jeffrey16@gmail.com", "nueva_clave_secreta");

        assertEquals("hash_nuevo", usuarioPrueba.getPassword());
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }
}
