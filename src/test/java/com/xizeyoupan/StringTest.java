package com.xizeyoupan;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class StringTest {
    @Test
    public void testString(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("msg","invalid token");
        System.out.println(map);
    }
}
