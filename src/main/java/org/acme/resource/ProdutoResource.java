package org.acme.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ProdutoRequestDTO;
import org.acme.dto.ProdutoResponseDTO;
import org.acme.service.ProdutoService;

@Path("/produtos")
public class ProdutoResource {

    ProdutoService produtoService;

    public ProdutoResource(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarProdutos() {
        return Response.ok(produtoService.listarProdutos()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response consultarProdutoPorId(Long id) {
        return Response.ok(produtoService.consultarProdutoPorId(id)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        return Response.ok(produtoService.criarProduto(produtoRequestDTO)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editarProduto(@PathParam("id") Long id, ProdutoRequestDTO produtoRequestDTO) {
        return Response.ok(produtoService.editarProduto(id, produtoRequestDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response excluirProduto(@PathParam("id") Long id) {
        produtoService.excluirProduto(id);
        return Response.noContent().build();
    }
}
