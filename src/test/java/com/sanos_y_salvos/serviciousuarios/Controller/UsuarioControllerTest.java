package com.sanos_y_salvos.serviciousuarios.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanos_y_salvos.serviciousuarios.Assemblers.UsuarioModelAssembler;
import com.sanos_y_salvos.serviciousuarios.DTO.PasswordChangeDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.UserProfileDTO;
import com.sanos_y_salvos.serviciousuarios.Jwt.JwtUtil;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    //no pasaban con el autowired¿
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioModelAssembler usuarioAssembler;

    @MockitoBean
    private JwtUtil jwtUtil;


    @BeforeEach
    void setUpSecurityContext() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("vicente@gmail.com");
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void listarActivos_DeberiaRetornar200Ok() throws Exception {
        Usuario u = new Usuario(); u.setId(1L); u.setEmail("vicente@gmail.com");
        when(usuarioService.findAllActive()).thenReturn(List.of(u));
        when(usuarioAssembler.toModel(any(Usuario.class))).thenReturn(EntityModel.of(u));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerPorUsername_DeberiaRetornar200Ok() throws Exception {
        Usuario u = new Usuario(); u.setEmail("vicente@gmail.com");
        when(usuarioService.findByEmail("vicente@gmail.com")).thenReturn(u);
        when(usuarioAssembler.toModel(any(Usuario.class))).thenReturn(EntityModel.of(u));

        mockMvc.perform(get("/api/v1/usuarios/vicente@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerMiPerfil_DeberiaRetornar200Ok() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        when(usuarioService.getProfile("vicente@gmail.com")).thenReturn(dto);

        mockMvc.perform(get("/api/v1/usuarios/me"))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarPerfil_DeberiaRetornar200Ok() throws Exception {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName("Vicente");
        when(usuarioService.updateProfile(eq("vicente@gmail.com"), any(UserProfileDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/v1/usuarios/vicente@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarMiPassword_DeberiaRetornar200Ok() throws Exception {
        PasswordChangeDTO dto = new PasswordChangeDTO("123", "321");
        doNothing().when(usuarioService).changeUserPassword(any(), any());

        mockMvc.perform(put("/api/v1/usuarios/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void resetearPasswordAdmin_DeberiaRetornar200Ok() throws Exception {
        PasswordChangeDTO dto = new PasswordChangeDTO(null, "nueva_clave");
        doNothing().when(usuarioService).adminResetPassword(any(), any());
        mockMvc.perform(put("/api/v1/usuarios/otro@gmail.com/admin-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void desactivarMiCuenta_DeberiaRetornar24NoContent() throws Exception {
        doNothing().when(usuarioService).deactivateUserByEmail("vicente@gmail.com");

        mockMvc.perform(delete("/api/v1/usuarios/me"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarCuenta_DeberiaRetornar204NoContent() throws Exception {
        doNothing().when(usuarioService).deleteByEmail("otro@gmail.com");

        mockMvc.perform(delete("/api/v1/usuarios/admin/force-delete/otro@gmail.com"))
                .andExpect(status().isNoContent());
    }
}

