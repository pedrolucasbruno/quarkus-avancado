package org.acme.exceptions;

import io.quarkus.arc.ArcUndeclaredThrowableException;
import jakarta.validation.ConstraintViolationException;
import org.acme.dto.ErrorDTO;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericoExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        ex.printStackTrace();

        if (ex instanceof GenericoException) {
            return Response.status(((GenericoException) ex).getStatusCode())
                    .entity(new ErrorDTO(ex.getMessage()))
                    .build();
        }

        if (ex instanceof ArcUndeclaredThrowableException) {
            return Response.status(400)
                    .entity(new ErrorDTO("Campos obrigatórios não informados"))
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorDTO("Erro interno do servidor. Contate o suporte."))
                .build();
    }
}