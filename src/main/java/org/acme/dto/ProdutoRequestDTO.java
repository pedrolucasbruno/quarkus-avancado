package org.acme.dto;

public record ProdutoRequestDTO(
        String nome,
        String descricao,
        Double preco,
        int estoque
) {
}
