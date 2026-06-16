package com.sanos_y_salvos.serviciousuarios.Assemblers;


import com.sanos_y_salvos.serviciousuarios.Controller.UsuarioController;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UsuarioModelAssembler implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario){
        return EntityModel.of(usuario,
                linkTo(methodOn(UsuarioController.class).obtenerPorUsername(usuario.getEmail())).withSelfRel(),
                linkTo(methodOn(UsuarioController.class).listarActivos()).withRel("usuarios"));
    }
}
