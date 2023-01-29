package com.kabigon.kabigonapiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.kabigon.kabigonapiclientsdk.model.User;
import com.kabigon.kabigonapiclientsdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * 调用第三方接口的客户端
 *
 * @author kabigon
 * @version 2023/1/2/21:55
 */
@Slf4j
public class KabigonApiClient {

    private final String accessKey;

    private final String secretKey;

    public KabigonApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public String getNameByGet(String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        String result = HttpUtil.get("http://127.0.0.1:8090/api/name/get", params);
        log.info("getNameByGet ===> [{}]", result);
        return result;
    }

    public String getNameByPost(String name) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("name", name);
        String result = HttpUtil.post("http://127.0.0.1:8090/api/name/post", params);
        log.info("getNameByPost ===> [{}]", result);
        return result;
    }

    public String getUserNameByPost(User user) {
        String userstr = JSONUtil.toJsonStr(user);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("accessKey", accessKey);
        // 一定不能直接传
        //headers.put("secretKey", "asdfghjkl");
        headers.put("nonce", RandomUtil.randomNumbers(4));
        headers.put("timestamp", String.valueOf(System.currentTimeMillis()));
        headers.put("sign", SignUtils.getSign(userstr, secretKey));
        headers.put("body", userstr);

        HttpResponse response = HttpRequest.post("http://127.0.0.1:8090/api/name/user")
                .addHeaders(headers)
                .body(userstr)
                .execute();
        log.info("getUserNameByPost ===> [{}]", response.body());
        return response.body();
    }

}
