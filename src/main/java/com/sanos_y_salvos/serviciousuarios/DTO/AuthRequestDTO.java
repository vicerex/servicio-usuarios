package com.sanos_y_salvos.serviciousuarios.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @Schema(example = "email", description = "Correo registrado")
    private String email;
    @Schema(example = "contraseña", description = "Contraseña del usuario")
    private String password;
}