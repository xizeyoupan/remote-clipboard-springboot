package com.xizeyoupan.remoteclipboard.utils;

import com.xizeyoupan.remoteclipboard.entity.Clip;

public class KeyGenerator {
    public static String keyForConnection(String username, int id) {
        return keyForUser(username) + "connections/" + id + "/";
    }

    public static String keyForUser(String userName) {
        return "users/" + userName + "/";
    }

    public static String keyForFile(Clip clip) {
        return keyForUser(clip.getUsername()) + "file/" + clip.getFileName() +  "/uuid/" + clip.getUuid();
    }
}
