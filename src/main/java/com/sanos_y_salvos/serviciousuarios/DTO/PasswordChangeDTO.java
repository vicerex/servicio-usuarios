package com.sanos_y_salvos.serviciousuarios.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
}
