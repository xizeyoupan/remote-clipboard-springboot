package com.xizeyoupan.boot.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xizeyoupan.boot.service.TokenService;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public String getToken(String username, String connectionId, String password) {
        return JWT.create()
                .withClaim("username", username)
                .withClaim("connectionId", connectionId)
                .sign(Algorithm.HMAC256(password));
    }

    @Override
    public boolean validToken(String token, String username, String connectionId, String password) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(password))
                .withClaim("username", username)
                .withClaim("connectionId", connectionId)
                .build();

        try {
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
