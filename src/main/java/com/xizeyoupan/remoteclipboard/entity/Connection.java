package com.xizeyoupan.remoteclipboard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Connection {
    private String username;
    private Integer id;
    private Integer version;
    private String token;
}
