package io.cc.gateway.route;

import java.util.List;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author nhsoft.lsd
 */
public class PathRouteSelector implements RouteSelector {

    private AntPathMatcher matcher = new AntPathMatcher();

    private List<Route> routes;

    public PathRouteSelector(final List<Route> routes) {
       this.routes = routes;
    }

    @Override
    public Route selectOne(final ServerWebExchange exchange) {
        if (routes.isEmpty()) {
            throw new RuntimeException("route is empty");
        }

        String path = exchange.getRequest().getPath().value();

        for (Route route : routes) {
            if (matcher.match(route.getPath(), path)) {
                return route;
            }
        }
        return null;
    }
}
