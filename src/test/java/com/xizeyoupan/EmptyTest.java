package com.xizeyoupan;

import org.junit.jupiter.api.Test;
import org.springframework.util.ObjectUtils;


public class EmptyTest {

    @Test
    public void testEmpty(){
        assert !ObjectUtils.isEmpty(0);
    }
}
