package org.acme.resource;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.dto.LoginRequestDTO;
import org.acme.dto.UsuarioRequestDTO;
import org.acme.service.UsuarioService;

@Path("/auth")
public class UsuarioResource {

    UsuarioService usuarioService;

    public UsuarioResource(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @POST
    @Path("/register")
    public Response criarUsuario(UsuarioRequestDTO usuarioRequest) {
        return Response.ok().entity(usuarioService.criarUsuario(usuarioRequest)).build();
    }

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO loginRequest) {
        if (usuarioService.login(loginRequest) != null) {
            return Response.ok().entity(usuarioService.login(loginRequest)).build();
        }
        return Response.status(Response.Status.FORBIDDEN).build();
    }
}
