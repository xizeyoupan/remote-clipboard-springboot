package com.xizeyoupan.remoteclipboard.entity;

import lombok.Data;

import java.util.List;

@Data
public class Index {
    private String adapter;
    private String dirname;
    private List<File> files;
    private List<String> storages;

}
