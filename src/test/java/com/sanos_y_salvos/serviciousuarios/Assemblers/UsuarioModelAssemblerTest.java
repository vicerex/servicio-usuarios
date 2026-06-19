package com.sanos_y_salvos.serviciousuarios.Assemblers;

import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioModelAssemblerTest {

    private final UsuarioModelAssembler assembler = new UsuarioModelAssembler();

    @Test
    void toModel_DeberiaAgregarLinksCorrectamente() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@gmail.com");

        EntityModel<Usuario> model = assembler.toModel(usuario);

        assertNotNull(model);
        assertEquals("test@gmail.com", model.getContent().getEmail());

        assertTrue(model.hasLink("self"), "El modelo debería tener un link 'self'");
    }
}