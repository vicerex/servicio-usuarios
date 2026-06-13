package com.sanos_y_salvos.serviciousuarios.Controller;


import com.sanos_y_salvos.serviciousuarios.DTO.PasswordChangeDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.UserProfileDTO;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios activos", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<List<Usuario>> listarActivos() {
        List<Usuario> usuarios = usuarioService.findAllActive();
        return ResponseEntity.ok(usuarios);
    }

    @PreAuthorize("hasAnyAuthority('ROL_ADMIN')")
    @GetMapping("/{username}")
    @Operation(summary = "Obtener usuario por email", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<Usuario> obtenerPorUsername(@PathVariable String username) {
        Usuario usuario = usuarioService.findByEmail(username);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener mi perfil", security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<UserProfileDTO> obtenerMiPerfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return ResponseEntity.ok(usuarioService.getProfile(email));
    }

    @PutMapping("/{username}")
    @PreAuthorize("#username == authentication.name or hasAuthority('ROL_ADMIN')")
    @Operation(
            summary = "Actualizar perfil(Usuario o Admin)",
            description = "Actualiza el nombre y dirección. El usuario puede editar sus datos o un admin puede editar los de cualquier usuario",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<UserProfileDTO> actualizarPerfil(
            @PathVariable String username,
            @RequestBody UserProfileDTO updateProfileDTO){

        return ResponseEntity.ok(usuarioService.updateProfile(username, updateProfileDTO));
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cambiar mi contraseña (Usuario)",
            description = "Requiere contraseña actual",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<String> cambiarMiPassword(@RequestBody PasswordChangeDTO passwordDTO){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        usuarioService.changeUserPassword(email, passwordDTO);

        return ResponseEntity.ok("Contraseña actualizada exitosamente");
    }

    @PutMapping("/{username}/admin-password")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(
            summary = "Resetear contraseña (Admin)",
            description = "Fuerza una nueva contraseña sin necesidad de saber la anterior",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<String> resetearPasswordAdmin(
            @PathVariable String username,
            @RequestBody PasswordChangeDTO passwordDTO) {

        usuarioService.adminResetPassword(username, passwordDTO.getNewPassword());

        return ResponseEntity.ok("Contraseña reseteada por Administrador");
    }


    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Desactivar mi cuenta",
            description = "El usuario desactiva su propia cuenta",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> desactivarMiCuenta(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        usuarioService.deactivateUserByEmail(email);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/force-delete/{username}")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @Operation(
            summary = "Eliminar usuario (Admin)",
            description = "El administrador puede borrar para siempre una cuenta",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<Void> eliminarCuenta(
            @PathVariable String username) {
        usuarioService.deleteByEmail(username);

        return ResponseEntity.noContent().build();
    }


}
