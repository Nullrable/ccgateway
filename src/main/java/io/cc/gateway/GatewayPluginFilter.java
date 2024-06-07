package io.cc.gateway;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public interface GatewayPluginFilter {

    Mono<Void> filter(ServerWebExchange exchange);
}
