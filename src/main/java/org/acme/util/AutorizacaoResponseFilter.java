package org.acme.util;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.acme.dto.ErrorDTO;

import java.io.IOException;

@Provider
public class AutorizacaoResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        if (responseContext.getStatus() == 401) {
            responseContext.setEntity(new ErrorDTO("Não autenticado"));
        }

        if (responseContext.getStatus() == 403) {
            responseContext.setEntity(new ErrorDTO("Acesso negado"));
        }
    }
}
