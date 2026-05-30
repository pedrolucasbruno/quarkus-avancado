package org.acme.dto;

import jakarta.persistence.Column;

public record UsuarioResponseDTO(String nome, String email, String senha, String role) {
}
