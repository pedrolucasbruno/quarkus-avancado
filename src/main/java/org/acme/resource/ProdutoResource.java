package org.acme.resource;

import io.quarkus.security.Authenticated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.dto.ProdutoRequestDTO;
import org.acme.service.ProdutoService;

@Path("/produtos")
public class ProdutoResource {

    ProdutoService produtoService;

    public ProdutoResource(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response listarProdutos() {
        return Response.ok(produtoService.listarProdutos()).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Authenticated
    public Response consultarProdutoPorId(Long id) {
        return Response.ok(produtoService.consultarProdutoPorId(id)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response criarProduto(ProdutoRequestDTO produtoRequestDTO) {
        return Response.ok(produtoService.criarProduto(produtoRequestDTO)).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("ADMIN")
    public Response editarProduto(@PathParam("id") Long id, ProdutoRequestDTO produtoRequestDTO) {
        return Response.ok(produtoService.editarProduto(id, produtoRequestDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed("ADMIN")
    public Response excluirProduto(@PathParam("id") Long id) {
        produtoService.excluirProduto(id);
        return Response.noContent().build();
    }
}
