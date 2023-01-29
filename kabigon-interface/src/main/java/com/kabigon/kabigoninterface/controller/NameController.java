package com.kabigon.kabigoninterface.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.kabigon.kabigonapiclientsdk.model.User;
import com.kabigon.kabigonapiclientsdk.util.SignUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @author kabigon
 * @version 2023/1/23/21:44
 */
@RestController
@RequestMapping("/name")
public class NameController {

    @GetMapping("/get")
    public String getNameByGet(String name) {
        return "Get 你的名字是" + name;
    }

    @PostMapping("/post")
    public String getNameByPost(@RequestParam String name) {
        return "Post 你的名字是" + name;
    }

    @PostMapping("/user")
    public String getUserNameByPost(@RequestBody User user, HttpServletRequest request) {
        String accessKey = request.getHeader("accessKey");
        //String secretKey = request.getHeader("secretKey");
        String nonce = request.getHeader("nonce");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        String body = JSONUtil.toJsonStr(user);
        // 直接传递 secretKey 不安全
        //if (!accessKey.equals("kabigon") || !secretKey.equals("asdfghjkl")) {
        //    throw new RuntimeException("参数错误");
        //}

        //// TODO 实际情况中，在数据库中查到accessKey是否分配给用户
        //if (!"kabigon".equals(accessKey)) {
        //    throw new RuntimeException("无权限");
        //}
        //// 时间和当前时间不能超过五分钟 5 * 60 * 1000
        //Date endData = new Date(Long.parseLong(timestamp) + 5 * 60 * 1000);
        //if (!DateUtil.isIn(new Date(), new Date(Long.parseLong(timestamp)), endData)) {
        //    throw new RuntimeException("时间超时，无权限");
        //}
        //// 用户请求次数限制
        //if (Long.parseLong(nonce) > 10000) {
        //    throw new RuntimeException("请求次数过多，无权限");
        //}
        //// todo 实际情况中，在数据库中查出secretKey
        //String serverSign = SignUtils.getSign(body, "asdfghjkl");
        //if (!sign.equals(serverSign)) {
        //    throw new RuntimeException("无权限");
        //}
        //int count = 1/ 0;
        return "Post 用户的名字是" + user.getUserName();
    }

}
