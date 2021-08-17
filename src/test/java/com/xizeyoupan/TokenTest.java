package com.xizeyoupan;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class TokenTest {
    @Test
    public void testToken() {
        String token = JWT.create()
                .withClaim("username", "username")
                .withClaim("connectionId", 2)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 5000))
                .sign(Algorithm.HMAC256("psd"));

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256("psd"))
                .build();

        verifier.verify(token);
    }
}
