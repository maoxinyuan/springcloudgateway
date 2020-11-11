package com.pacific.gateway.core.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.pacific.gateway.common.JsonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("custom global filter");
        return doFilter(exchange, chain);
    }

    protected abstract Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain);

    /**
     * 失败响应
     * @param code 状态码
     * @param message 错误消息
     * @return
     */
    protected Mono<Void> fail(ServerWebExchange exchange,String code,String message){
        JsonResult result = new JsonResult(false, code, message);
        byte[] bytes = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    /**
     * 前置过滤
     * @param exchange
     * @param chain
     * @return
     */
    protected Mono<Void> preSuccess(ServerWebExchange exchange, GatewayFilterChain chain){
        return chain.filter(exchange);
    }

    /**
     * 后置过滤
     * @param exchange
     * @param chain
     * @return
     */
    protected Mono<Void> postSuccess(ServerWebExchange exchange, GatewayFilterChain chain){
        return chain.filter(exchange).then();
    }

}
