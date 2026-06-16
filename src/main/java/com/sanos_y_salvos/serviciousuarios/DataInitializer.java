package com.sanos_y_salvos.serviciousuarios;

import com.sanos_y_salvos.serviciousuarios.Model.Rol;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner crearAdminPorDefecto(UsuarioRepository usuarioRepository,
                                                  PasswordEncoder passwordEncoder) {
        return args -> {

            String adminEmail = "admin@sanosysalvos.cl";

            if (usuarioRepository.findByEmail("admin@sanosysalvos.cl").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setName("Administrador Sistema");
                admin.setPassword(passwordEncoder.encode("admin123*"));
                admin.setEmail(adminEmail);
                admin.setRol(Rol.ROL_ADMIN);
                admin.setPhone("000000000");
                admin.setAddress("Direccion por defecto");
                admin.setActivo(true);

                usuarioRepository.save(admin);
                System.out.println("Usuario admin creado: " + adminEmail);
            } else {
                System.out.println("Admin ya existe, no se creo otro.");
            }
        };
    }
}