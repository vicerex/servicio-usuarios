package com.sanos_y_salvos.serviciousuarios.Controller;


import com.sanos_y_salvos.serviciousuarios.DTO.AuthRequestDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.AuthResponseDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.RegistroConRolDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.RegistroDTO;
import com.sanos_y_salvos.serviciousuarios.Jwt.JwtUtil;
import com.sanos_y_salvos.serviciousuarios.Model.Rol;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Autenticacion", description = "Operaciones relacionadas con resgistros/inicios de sesion")
@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    @Operation(
            summary = "Registro de Dueño/Rescatista",
            description = "Registro público para nuevos usuarios (rol por defecto: ROL_DUENO)"
    )
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroDTO registro) {
        if (usuarioService.existsByEmail(registro.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado...");
        }
        Usuario usuario = new Usuario();
        usuario.setName(registro.getName());
        usuario.setPassword(registro.getPassword());
        usuario.setEmail(registro.getEmail());
        usuario.setRol(Rol.ROL_DUENO);
        usuario.setAddress(registro.getAddress());
        usuario.setPhone(registro.getPhone());
        usuario.setActivo(true);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.save(usuario));
    }

    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    @PostMapping("/registro/admin")
    @Operation(
            summary = "Registro admin (Asignación de roles)",
            description = "Un admin puede registrar usuarios con cualquier rol (CLINICA, REFUGIO, MUNICIPALIDAD)",
            security = @SecurityRequirement(name = "bearer-jwt")
    )
    public ResponseEntity<String> registrarConRol(@RequestBody RegistroConRolDTO registro) {
        if (usuarioService.existsByEmail(registro.getCorreo())){
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setName(registro.getUsername());
        usuario.setPassword(registro.getPassword());
        usuario.setEmail(registro.getCorreo());
        usuario.setRol(registro.getRol()); // El admin elige el rol del enum
        usuario.setAddress(registro.getAddress());
        usuario.setActivo(true);
        usuarioService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario creado exitosamente con rol: " + registro.getRol());
    }

    @PostMapping("/ingreso")
    @Operation(
            summary = "Inicio de sesión",
            description = "Autentica el usuario y devuelve el token jwt"
    )
    public ResponseEntity<AuthResponseDTO> ingresar(@RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = usuarioService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }
}
