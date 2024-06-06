package io.cc.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public abstract class AbstractGatewayPlugin implements GatewayPlugin {

    public Mono<Void> doHandle(ServerWebExchange exchange, GatewayPluginChain chain) {
        return null;
    }

    public boolean doSupports(ServerWebExchange exchange) {
        return false;
    }

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final GatewayPluginChain chain) {
        boolean supports = doSupports(exchange);
        return supports ? doHandle(exchange, chain) : chain.handle(exchange);
    }

    @Override
    public boolean supports(final ServerWebExchange exchange) {
        return doSupports(exchange);
    }
}
