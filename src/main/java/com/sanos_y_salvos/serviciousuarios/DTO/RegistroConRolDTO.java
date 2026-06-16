package com.sanos_y_salvos.serviciousuarios.DTO;

import com.sanos_y_salvos.serviciousuarios.Model.Rol;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RegistroConRolDTO {

    @Schema(example = "usuario", description = "Nombre de usuario registrado")
    private String username;
    @Schema(example = "correo", description = "Correo del usuario")
    private String correo;
    @Schema(example = "contraseña", description = "Contraseña del usuario")
    private String password;
    @Schema(example = "direccion", description = "Direccion del usuario")
    private String address;
    @Schema(example = "ROL_DUENO", description = "Roles: ROL_DUENO, ROL_RESCATISTA, ROL_CLINICA, ROL_REFUGIO, ROL_MUNICIPALIDAD")
    private Rol rol;


}
