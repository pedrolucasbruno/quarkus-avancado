package org.acme.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.acme.dto.ProdutoRequestDTO;
import org.acme.dto.ProdutoResponseDTO;
import org.acme.entity.Produto;
import org.acme.repository.ProdutoRepository;

import java.util.List;

@ApplicationScoped
public class ProdutoService {

    ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<ProdutoResponseDTO> listarProdutos() {
        return produtoRepository.listAll().stream()
                .map(this::mapToProdutoResponseDTO)
                .toList();
    }

    public ProdutoResponseDTO consultarProdutoPorId(Long id) {
        return mapToProdutoResponseDTO(produtoRepository.findById(id));
    }

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = mapToProduto(produtoRequestDTO);
        produto.persist();
        return mapToProdutoResponseDTO(produto);
    }

    @Transactional
    public ProdutoResponseDTO editarProduto(Long id, ProdutoRequestDTO produtoRequestDTO) {
        Produto produto = produtoRepository.findById(id);
        produto.setDescricao(produtoRequestDTO.descricao());
        produto.setNome(produtoRequestDTO.nome());
        produto.setPreco(produtoRequestDTO.preco());
        produto.setEstoque(produtoRequestDTO.estoque());
        produto.persist();
        return mapToProdutoResponseDTO(produto);
    }

    @Transactional
    public void excluirProduto(Long id) {
        produtoRepository.findById(id).delete();
    }

    public ProdutoResponseDTO mapToProdutoResponseDTO(Produto produto) {
        return new ProdutoResponseDTO(produto.getId(),
                                      produto.getNome(),
                                      produto.getDescricao(),
                                      produto.getPreco(),
                                      produto.getEstoque()
        );
    }

    public Produto mapToProduto(ProdutoRequestDTO produtoRequestDTO) {
        return new Produto(produtoRequestDTO.nome(),
                           produtoRequestDTO.descricao(),
                           produtoRequestDTO.preco(),
                           produtoRequestDTO.estoque());
    }
}
