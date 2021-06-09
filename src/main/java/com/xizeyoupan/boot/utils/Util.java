package com.xizeyoupan.boot.utils;

import java.util.UUID;

public class Util {
    public static String getUUID(String str) {
        return UUID.nameUUIDFromBytes(str.getBytes()).toString();
    }
}
