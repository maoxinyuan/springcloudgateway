package com.pacific.gateway.core.gateway.filter;

import com.pacific.gateway.common.Constant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class TokenFilter extends AbstractGlobalFilter {

    @Override
    protected Mono<Void> doFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst(Constant.TOKEN);
        if (token == null || Constant.EMPTY_STRING == token.trim()) {
            return fail(exchange, String.valueOf(HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED.getReasonPhrase());
        }
        return preSuccess(exchange, chain);
    }

    @Override
    public int getOrder() {
        return FilterOrder.TOKEN_ACCESS_ORDER;
    }

}
