package com.sanos_y_salvos.serviciousuarios.Service;

import com.sanos_y_salvos.serviciousuarios.DTO.PasswordChangeDTO;
import com.sanos_y_salvos.serviciousuarios.DTO.UserProfileDTO;
import com.sanos_y_salvos.serviciousuarios.Model.Usuario;
import com.sanos_y_salvos.serviciousuarios.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    /* Este trae usuarios activos e inactivos. ###falta discutirlo###
    public List<Usuario> findAll(){ return usuarioRepository.findAll(); }
    */

    public List<Usuario> findAllActive(){
        return usuarioRepository.findByActivoTrue();
    }

    public Usuario findByEmail(String email){
        return usuarioRepository.findByEmail(email)
                .filter(Usuario::isActivo)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado o Desactivado"));
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);  }


    public void deleteByEmail(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    public Usuario save(Usuario usuario){
        if (usuario.getPassword() == null || usuario.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía...");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!user.isActivo()) {
            throw new UsernameNotFoundException("Cuenta Desactivada");
        }
        return new User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRol().name())));
    }

    @Transactional
    public UserProfileDTO updateProfile(String aunthenticatedEmail, UserProfileDTO updateDto) {
        Usuario usuario = findByEmail(aunthenticatedEmail);

        if (updateDto.getName() != null && !updateDto.getName().trim().isEmpty()){
            usuario.setName(updateDto.getName());
        }
        if (updateDto.getAddress() != null){
            usuario.setAddress(updateDto.getAddress());
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapToUserProfileDTO(usuarioActualizado);

    }

    public void changeUserPassword(String email, PasswordChangeDTO dto){
        Usuario usuario = findByEmail(email);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), usuario.getPassword())){
            throw new IllegalArgumentException("La contraseña actual es incorrecta...");
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuarioRepository.save(usuario);
    }

    public void adminResetPassword(String targetEmail, String newPassword) {
        Usuario usuario = findByEmail(targetEmail);

        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }

    private UserProfileDTO mapToUserProfileDTO(Usuario usuario) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName(usuario.getName());
        dto.setEmail(usuario.getEmail());
        dto.setAddress(usuario.getAddress());

        return dto;
    }



}
