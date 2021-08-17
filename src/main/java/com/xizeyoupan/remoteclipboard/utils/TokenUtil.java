package com.xizeyoupan.remoteclipboard.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xizeyoupan.remoteclipboard.dao.UserDao;
import com.xizeyoupan.remoteclipboard.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class TokenUtil {

    final UserDao userDao;

    private final static Integer longExpires = 1000 * 3600 * 24 * 7;

    public TokenUtil(UserDao userDao) {
        this.userDao = userDao;
    }

    public static String getToken(User user, int connectionId) {
        return JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("connectionId", connectionId)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + longExpires))
                .sign(Algorithm.HMAC256(user.getPassword()));
    }

    public static String getUsernameByToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("username").asString();
    }

    public static Integer getConnectionIdByToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("connectionId").asInt();
    }

    public boolean validToken(String token) {

        String username;
        try {
            username = getUsernameByToken(token);
        } catch (Exception e) {
            log.error("Token error : The Claim 'username' value can't be parsed.");
            return false;
        }

        User user = userDao.getByUsername(username);

        if (ObjectUtils.isEmpty(user)) {
            log.error("Token error : No such user.");
            return false;
        }

        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();

        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            log.error("Token verify error, " + e);
            return false;
        }
    }
}
