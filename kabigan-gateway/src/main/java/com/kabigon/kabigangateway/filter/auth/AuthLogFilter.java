package com.kabigon.kabigangateway.filter.auth;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.kabigon.commonKabigon.model.entity.InterfaceInfo;
import com.kabigon.commonKabigon.model.entity.User;
import com.kabigon.commonKabigon.model.service.InnerInterfaceInfoService;
import com.kabigon.commonKabigon.model.service.InnerUserService;
import com.kabigon.kabigonapiclientsdk.util.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.rmi.CORBA.Tie;
import java.util.Date;
import java.util.Optional;

/**
 * 用户权限校验
 * @author kabigon
 * @version 2023/1/28/01:02
 */
@Slf4j
@Component
public class AuthLogFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserService innerUserService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        //String secretKey = request.getHeader("secretKey");
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
        // 判断所有必填参数是否为空
        if (StrUtil.hasBlank(accessKey, nonce, timestamp, sign, body)) {
            return handleNoAuth(exchange);
        }

        // 直接传递 secretKey 不安全
        //if (!accessKey.equals("kabigon") || !secretKey.equals("asdfghjkl")) {
        //    throw new RuntimeException("参数错误");
        //}

        // 实际情况中，在数据库中查到accessKey是否分配给用户
        User invokeUser = null;

        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(exchange);
        }

        if (!invokeUser.getAccessKey().equals(accessKey)) {
            return handleNoAuth(exchange);
        }
        // 时间和当前时间不能超过五分钟 5 * 60 * 1000
        Date endData = new Date(Long.parseLong(timestamp) + 5 * 60 * 1000);
        if (!DateUtil.isIn(new Date(), new Date(Long.parseLong(timestamp)), endData)) {
            return handleNoAuth(exchange);
        }
        // 用户请求次数限制
        if (Long.parseLong(nonce) > 10000) {
            return handleNoAuth(exchange);
        }
        // 实际情况中，在数据库中查出secretKey
        String serverSign = SignUtils.getSign(body, invokeUser.getSecretKey());
        if (!serverSign.equals(sign)) {
            return handleNoAuth(exchange);
        }

        // 请求的接口是否存在，以及请求方法是否匹配
        String requestUrl = request.getURI().getRawPath();
        String url = "http://localhost:8123" + requestUrl;
        String methodValue = request.getMethodValue();
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, methodValue);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }
        if (interfaceInfo == null) {
            return handleNoAuth(exchange);
        }
        return chain.filter(exchange);
    }

    private static Mono<Void> handleNoAuth(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
