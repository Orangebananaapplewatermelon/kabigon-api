package com.kabigon.project.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author kabigon
 * @version 2023/1/26/15:11
 */
@SpringBootTest
public class UserInterfaceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
        boolean b = userInterfaceInfoService.invokeCount(54L, 1);
        Assertions.assertTrue(b);
    }


}
