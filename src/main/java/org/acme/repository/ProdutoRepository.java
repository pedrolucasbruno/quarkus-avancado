package org.acme.repository;

import io.quarkus.hibernate.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.entity.Produto;

@ApplicationScoped
public class ProdutoRepository implements PanacheRepository<Produto> {
}
