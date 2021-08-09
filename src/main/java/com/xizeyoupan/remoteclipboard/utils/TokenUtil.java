package com.xizeyoupan.remoteclipboard.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xizeyoupan.remoteclipboard.entity.Connection;
import com.xizeyoupan.remoteclipboard.entity.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class TokenUtil {
    public static String getToken(User user, Connection connection) {
        return JWT.create()
                .withClaim("UserName", user.getUserName())
                .withClaim("connectionId", connection.getId())
                .sign(Algorithm.HMAC256(user.getHashPassword()));
    }

    public static String getToken(User user) {
        int expires = 5 * 1000;
        return JWT.create()
                .withClaim("UserName", user.getUserName())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + expires))
                .sign(Algorithm.HMAC256(user.getHashPassword()));
    }

    public static boolean validToken(User user, String token) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getHashPassword()))
                .withClaim("UserName", user.getUserName())
                .build();

        try {
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }

    public static boolean validToken(User user, Connection connection, String token) throws Exception {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getHashPassword()))
                .withClaim("UserName", user.getUserName())
                .withClaim("connectionId", connection.getId())
                .build();

        try {
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.debug(e.getMessage());
            return false;
        }
    }
}
