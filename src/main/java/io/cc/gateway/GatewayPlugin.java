package io.cc.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public interface GatewayPlugin {

    Mono<Void> handle(ServerWebExchange exchange, GatewayPluginChain chain);

    boolean supports(ServerWebExchange exchange);
}
