package com.sanos_y_salvos.serviciousuarios.Controller;



import com.sanos_y_salvos.serviciousuarios.DTO.RegistroDTO;
import com.sanos_y_salvos.serviciousuarios.Jwt.JwtUtil;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Test
    void registrar_ConDatosValidos_DeberiaRetornar201Creado() throws Exception {

        RegistroDTO registroDTO = new RegistroDTO();
        registroDTO.setName("Vicencio");
        registroDTO.setEmail("Vicencio@gmail.com");
        registroDTO.setPassword("12345");
        registroDTO.setAddress("Mi casa #120");
        registroDTO.setPhone("123456789");

        Usuario usuarioCreado = new Usuario();
        usuarioCreado.setId(1L);
        usuarioCreado.setName("Vicencio");
        usuarioCreado.setEmail("Vicencio@gmail.com");

        when(usuarioService.existsByEmail(any())).thenReturn(false);
        when(usuarioService.save(any(Usuario.class))).thenReturn(usuarioCreado);

        mockMvc.perform(post("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDTO)))

                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.name").value("Vicencio"))
                .andExpect(jsonPath("$.email").value("Vicencio@gmail.com"));

    }

}
