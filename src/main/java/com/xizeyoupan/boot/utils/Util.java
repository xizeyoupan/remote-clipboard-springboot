package com.xizeyoupan.boot.utils;

import java.util.UUID;

public class Util {
    public static String getUUID(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes()).toString();
    }

    public static String keyForClips(String username) {
        return "user/" + username + "/clips";
    }

    public static String keyForPassword(String username) {
        return "user/" + username + "/password";
    }

    public static String keyForUser(String username) {
        return "user/" + username;
    }


}
