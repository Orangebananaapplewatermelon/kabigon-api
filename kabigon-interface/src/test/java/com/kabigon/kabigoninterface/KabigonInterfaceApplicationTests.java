package com.kabigon.kabigoninterface;

import com.kabigon.kabigonapiclientsdk.client.KabigonApiClient;
import com.kabigon.kabigonapiclientsdk.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootTest
class KabigonInterfaceApplicationTests {

    @Resource
    private KabigonApiClient kabigonApiClient;

    @Test
    void contextLoads() {
        User user = new User();
        user.setUserName("alice");
        String userNameByPost = kabigonApiClient.getUserNameByPost(user);
        System.out.println(userNameByPost);
    }

}
