package com.xizeyoupan.boot.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "user-default")
public class User {
    private String username;
    private String avatarUrl;
    private String token;
    private long currentConnectionId;

    public User(String username, String token, long currentConnectionId) {
        this.username = username;
        this.token = token;
        this.currentConnectionId = currentConnectionId;
    }

}
