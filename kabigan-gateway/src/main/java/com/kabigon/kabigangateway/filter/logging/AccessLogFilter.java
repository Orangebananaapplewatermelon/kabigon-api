package com.kabigon.kabigangateway.filter.logging;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.kabigon.commonKabigon.model.entity.InterfaceInfo;
import com.kabigon.commonKabigon.model.entity.User;
import com.kabigon.commonKabigon.model.service.InnerInterfaceInfoService;
import com.kabigon.commonKabigon.model.service.InnerUserInterfaceInfoService;
import com.kabigon.commonKabigon.model.service.InnerUserService;
import com.kabigon.kabigangateway.util.WebFrameworkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.date.DatePattern.NORM_DATETIME_MS_FORMATTER;

/**
 * ??????????????????????????????
 *
 * TODO ????????????????????????????????????????????????????????????????????? https://github.com/Silvmike/webflux-demo/blob/master/tests/src/test/java/ru/hardcoders/demo/webflux/web_handler/filters/logging
 *
 * @author xu.biao
 */
@Slf4j
@Component
public class AccessLogFilter implements GlobalFilter, Ordered {

    @DubboReference
    private InnerUserInterfaceInfoService innerUserInterfaceInfoService;

    @DubboReference
    private InnerInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private InnerUserService innerUserService;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    /**
     * ????????????
     *
     * @param gatewayLog ????????????
     */
    private void writeAccessLog(AccessLog gatewayLog) {
        // ?????????????????? Logger ???????????? ELK ????????????
        // log.info("[writeAccessLog][???????????????{}]", JsonUtils.toJsonString(gatewayLog));

        // ??????????????????????????????????????????????????????
        // TODO ????????????

        // ???????????????????????????????????????????????????
        Map<String, Object> values = MapUtil.newHashMap(15, true); // ??????????????????????????????15 ??????????????????
        values.put("userId", gatewayLog.getUserId());
        values.put("userType", gatewayLog.getUserType());
        values.put("routeId", gatewayLog.getRoute() != null ? gatewayLog.getRoute().getId() : null);
        values.put("schema", gatewayLog.getSchema());
        values.put("requestUrl", gatewayLog.getRequestUrl());
        values.put("queryParams", gatewayLog.getQueryParams().toSingleValueMap());
        values.put("requestBody", JSONUtil.isTypeJSON(gatewayLog.getRequestBody()) ? // ?????? body ???????????????
                JSONUtil.parse(gatewayLog.getRequestBody()) : gatewayLog.getRequestBody());
        values.put("requestHeaders", JSONUtil.toJsonStr(gatewayLog.getRequestHeaders().toSingleValueMap()));
        values.put("userIp", gatewayLog.getUserIp());
        values.put("responseBody", JSONUtil.isTypeJSON(gatewayLog.getResponseBody()) ? // ?????? body ???????????????
                JSONUtil.parse(gatewayLog.getResponseBody()) : gatewayLog.getResponseBody());
        values.put("responseHeaders", gatewayLog.getResponseHeaders() != null ?
                JSONUtil.toJsonStr(gatewayLog.getResponseHeaders().toSingleValueMap()) : null);
        values.put("httpStatus", gatewayLog.getHttpStatus());
        values.put("startTime", LocalDateTimeUtil.format(gatewayLog.getStartTime(), NORM_DATETIME_MS_FORMATTER));
        values.put("endTime", LocalDateTimeUtil.format(gatewayLog.getEndTime(), NORM_DATETIME_MS_FORMATTER));
        values.put("duration", gatewayLog.getDuration() != null ? gatewayLog.getDuration() + " ms" : null);
        log.info("[writeAccessLog][???????????????{}]", JSONUtil.toJsonPrettyStr(values));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ??? Request ?????????????????????????????????????????????????????????
        ServerHttpRequest request = exchange.getRequest();
        // TODO traceId
        AccessLog gatewayLog = new AccessLog();
        gatewayLog.setRoute(WebFrameworkUtils.getGatewayRoute(exchange));
        gatewayLog.setSchema(request.getURI().getScheme());
        gatewayLog.setRequestMethod(request.getMethodValue());
        gatewayLog.setRequestUrl(request.getURI().getRawPath());
        gatewayLog.setQueryParams(request.getQueryParams());
        gatewayLog.setRequestHeaders(request.getHeaders());
        gatewayLog.setStartTime(LocalDateTime.now());
        gatewayLog.setUserIp(WebFrameworkUtils.getClientIP(exchange));

        // ?????? filter ??????
        MediaType mediaType = request.getHeaders().getContentType();
        if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)
                || MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) { // ?????? JSON ??? Form ???????????????
            return filterWithRequestBody(exchange, chain, gatewayLog);
        }
        return filterWithoutRequestBody(exchange, chain, gatewayLog);
    }

    private Mono<Void> filterWithoutRequestBody(ServerWebExchange exchange, GatewayFilterChain chain, AccessLog accessLog) {
        // ?????? Response??????????????? Response Body
        ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, accessLog);
        return chain.filter(exchange.mutate().response(decoratedResponse).build())
                .then(Mono.fromRunnable(() -> writeAccessLog(accessLog))); // ????????????
    }

    /**
     * ?????? {@link ModifyRequestBodyGatewayFilterFactory} ??????
     *
     * ???????????????????????? modifiedBody ????????? Request Body ??????
     */
    private Mono<Void> filterWithRequestBody(ServerWebExchange exchange, GatewayFilterChain chain, AccessLog gatewayLog) {
        // ?????? Request Body ?????????????????????????????????
        ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
        Mono<String> modifiedBody = serverRequest.bodyToMono(String.class).flatMap(body -> {
            gatewayLog.setRequestBody(body);
            return Mono.just(body);
        });

        // ?????? BodyInserter ??????
        BodyInserter<Mono<String>, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromPublisher(modifiedBody, String.class);
        // ?????? CachedBodyOutputMessage ??????
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(exchange.getRequest().getHeaders());
        // the new content type will be computed by bodyInserter (????????????????????????bodyInserter??????)
        // and then set in the request decorator (?????????????????????????????????)
        headers.remove(HttpHeaders.CONTENT_LENGTH); // ??????
        CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, headers);
        // ?????? BodyInserter ??? Request Body ????????? CachedBodyOutputMessage ???
        return bodyInserter.insert(outputMessage, new BodyInserterContext()).then(Mono.defer(() -> {
            // ?????? Request??????????????? Request Body
            ServerHttpRequest decoratedRequest = requestDecorate(exchange, headers, outputMessage);
            // ?????? Response??????????????? Response Body
            ServerHttpResponseDecorator decoratedResponse = recordResponseLog(exchange, gatewayLog);
            // ???????????????
            return chain.filter(exchange.mutate().request(decoratedRequest).response(decoratedResponse).build())
                    .then(Mono.fromRunnable(() -> writeAccessLog(gatewayLog))); // ????????????

        }));
    }

    /**
     * ??????????????????
     * ?????? DataBufferFactory ????????????????????????????????????
     */
    private ServerHttpResponseDecorator recordResponseLog(ServerWebExchange exchange, AccessLog gatewayLog) {
        ServerHttpResponse response = exchange.getResponse();
        return new ServerHttpResponseDecorator(response) {

            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    // ??????????????????
                    gatewayLog.setEndTime(LocalDateTime.now());
                    gatewayLog.setDuration((int) (LocalDateTimeUtil.between(gatewayLog.getStartTime(),
                            gatewayLog.getEndTime()).toMillis()));
                    // ??????????????????
                    //gatewayLog.setUserId(SecurityFrameworkUtils.getLoginUserId(exchange));
                    //gatewayLog.setUserType(SecurityFrameworkUtils.getLoginUserType(exchange));
                    gatewayLog.setResponseHeaders(response.getHeaders());
                    gatewayLog.setHttpStatus(response.getStatusCode());

                    // ?????????????????? + 1
                    HttpStatus statusCode = response.getStatusCode();
                    if (statusCode.value() == HttpStatus.OK.value()) {
                        ServerHttpRequest request = exchange.getRequest();
                        HttpHeaders headers = request.getHeaders();
                        String accessKey = headers.getFirst("accessKey");
                        User invokeUser = innerUserService.getInvokeUser(accessKey);
                        String requestUrl = request.getURI().getRawPath();
                        String url = "http://localhost:8123" + requestUrl;
                        String methodValue = request.getMethodValue();
                        InterfaceInfo interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(url, methodValue);
                        innerUserInterfaceInfoService.invokeCount(interfaceInfo.getId(), invokeUser.getId());
                    }

                    // ?????????????????????????????? json ?????????
                    String originalResponseContentType = exchange.getAttribute(ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                    if (StrUtil.isNotBlank(originalResponseContentType)
                            && originalResponseContentType.contains("application/json")) {
                        Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                        return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                            // ?????? response body ???????????????
                            byte[] content = readContent(dataBuffers);
                            String responseResult = new String(content, StandardCharsets.UTF_8);
                            gatewayLog.setResponseBody(responseResult);

                            // ??????
                            return bufferFactory.wrap(content);
                        }));
                    }
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
    }

    // ========== ?????? ModifyRequestBodyGatewayFilterFactory ???????????? ==========

    /**
     * ???????????????????????????????????? headers???body ??????
     *
     * @param exchange ??????
     * @param headers ?????????
     * @param outputMessage body ??????
     * @return ???????????????
     */
    private ServerHttpRequestDecorator requestDecorate(ServerWebExchange exchange, HttpHeaders headers, CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {

            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                } else {
                    // TODO: this causes a 'HTTP/1.1 411 Length Required' // on
                    // httpbin.org
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };
    }

    // ========== ?????? ModifyResponseBodyGatewayFilterFactory ???????????? ==========

    private byte[] readContent(List<? extends DataBuffer> dataBuffers) {
        // ???????????????????????????????????????????????????
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        DataBuffer join = dataBufferFactory.join(dataBuffers);
        byte[] content = new byte[join.readableByteCount()];
        join.read(content);
        // ???????????????
        DataBufferUtils.release(join);
        return content;
    }

}
