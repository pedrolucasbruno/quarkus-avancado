package org.acme.service;

import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.dto.LoginRequestDTO;
import org.acme.dto.LoginResponseDTO;
import org.acme.dto.UsuarioRequestDTO;
import org.acme.dto.UsuarioResponseDTO;
import org.acme.entity.Usuario;
import org.acme.exceptions.GenericoException;
import org.acme.repository.UsuarioRepository;
import org.acme.security.GeradorJWT;

@ApplicationScoped
public class UsuarioService {

    UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public UsuarioResponseDTO criarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        usuarioRepository.find("email", usuarioRequestDTO.email())
                .firstResultOptional()
                .ifPresent(u -> {throw new GenericoException(409, "E-mail já cadastrado");});

        Usuario usuario = new Usuario(usuarioRequestDTO.nome(),
                                      usuarioRequestDTO.email(),
                                      usuarioRequestDTO.senha(),
                                      usuarioRequestDTO.role());
        usuario.persist();
        return mapToUsuarioResponseDTO(usuario);
    }

    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        Usuario usuario = usuarioRepository.find("email", loginRequest.email()).firstResult();
        if (usuario != null && BcryptUtil.matches(loginRequest.senha(), usuario.getSenha())) {
            return new LoginResponseDTO(
                    GeradorJWT.gerarToken(usuario.getEmail(), usuario.getRole()),
                    "Bearer",
                    usuario.getRole());
        }
        throw new GenericoException(401, "E-mail ou senha inválidos");
    }

    public UsuarioResponseDTO mapToUsuarioResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getRole());
    }
}
