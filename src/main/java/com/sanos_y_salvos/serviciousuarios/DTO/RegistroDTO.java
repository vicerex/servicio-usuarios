package com.sanos_y_salvos.serviciousuarios.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegistroDTO {

    @Schema(example = "usuario", description = "Nombre de usuario registrado")
    private String name;
    @Schema(example = "email", description = "Email del usuario")
    private String email;
    @Schema(example = "contraseña", description = "Contraseña del usuario")
    private String password;
    @Schema(example = "address", description = "Direccion del usuario")
    private String address;
    @Schema(example = "phone", description = "Telefono del usuario")
    private String phone;
}
