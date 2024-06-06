package io.cc.gateway.route;

import org.springframework.web.server.ServerWebExchange;

/**
 * @author nhsoft.lsd
 */
public interface RouteSelector {

    Route selectOne(ServerWebExchange exchange);
}
