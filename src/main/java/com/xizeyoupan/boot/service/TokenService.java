package com.xizeyoupan.boot.service;


public interface TokenService {
    String getToken(String username, String connectionId, String password);

    boolean validToken(String token,String username, String connectionId, String password) throws Exception;
}
