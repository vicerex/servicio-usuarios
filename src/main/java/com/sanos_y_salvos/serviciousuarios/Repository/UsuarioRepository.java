package com.sanos_y_salvos.serviciousuarios.Repository;

import com.sanos_y_salvos.serviciousuarios.Model.Rol;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

        Optional<Usuario> findByEmail(String email);
        void deleteByEmail(String email);
        boolean existsByEmail(String email);


        List<Usuario>findByActivoTrue();
        List<Usuario> findByRol(Rol rol);


}
