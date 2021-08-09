package com.xizeyoupan.remoteclipboard.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Queue;

@Data
@AllArgsConstructor
public class Connection {
    private String userName;
    private int id;
    private Queue<Date> queue;
}
