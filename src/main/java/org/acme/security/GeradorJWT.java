package org.acme.security;

import io.smallrye.jwt.build.Jwt;

public class GeradorJWT {

    public static String gerarToken(String email, String role) {

        return Jwt.claims().
                issuer("http://localhost:8080/issuer")
                .upn(email)
                .groups(role)
                .expiresIn(3600)
                .sign();
    }
}
