package com.sanos_y_salvos.serviciousuarios.Jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    private final String SECRET = "esta_es_una_clave_secreta_muy_larga_de_32_caracteres_o_mas";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "SECRET_KEY", SECRET);
    }

    @Test
    void testGenerateAndValidateToken() {
        UserDetails user = new User("vicente@gmail.com", "pass",
                List.of(new SimpleGrantedAuthority("ROL_ADMIN"))); //admin para tener permisos xd

        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token, user));
        assertEquals("vicente@gmail.com", jwtUtil.extractUsername(token));
    }

    @Test
    void testTokenValidation_ConUsuarioErroneo_DeberiaFallar() {
        UserDetails user = new User("vicente@gmail.com", "password123", List.of());
        UserDetails otroUsuario = new User("juanito@gmail.com", "password123", List.of());

        String token = jwtUtil.generateToken(user);

        assertFalse(jwtUtil.validateToken(token, otroUsuario), "El token no debería ser válido para otro usuario");
    }
}