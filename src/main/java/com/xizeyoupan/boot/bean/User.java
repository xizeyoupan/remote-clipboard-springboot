package com.xizeyoupan.boot.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "user-default")
public class User {
    private String username;
    private String avatarUrl;
    private String token;
    private long connectionNumber;

    public User(String username, String token, long connectionNumber) {
        this.username = username;
        this.token = token;
        this.connectionNumber = connectionNumber;
    }

}
