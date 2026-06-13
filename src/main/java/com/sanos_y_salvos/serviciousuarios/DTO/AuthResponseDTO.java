package com.sanos_y_salvos.serviciousuarios.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    @Schema(example = "eyJhbGciOiJIUzI1NiJ9...", description = "Token JWT generado")
    private String jwt;
}