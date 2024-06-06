package io.cc.gateway.handler;

import io.cc.gateway.GatewayFilter;
import io.cc.gateway.GatewayFilterChain;
import io.cc.gateway.route.Route;
import io.cc.gateway.route.RouteSelector;
import io.cc.gateway.support.GatewayExchangeUtils;
import java.util.List;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public class FilteringWebHandler implements WebHandler {

    public static final String NAME = "filteringWebHandler";

    private final RouteSelector routerLoader;

    private final List<GatewayFilter> gatewayFilters;

    public FilteringWebHandler(final List<GatewayFilter> gatewayFilters, RouteSelector routerLoader) {
        this.gatewayFilters = gatewayFilters;
        this.routerLoader = routerLoader;
    }

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange) {
        Route route = routerLoader.selectOne(exchange);

        if (route == null) {
            Mono.error(new RuntimeException("route is not found"));
        }

        exchange.getAttributes().put(GatewayExchangeUtils.ROURE, route);

//        List<GatewayFilter> gatewayFilters = route.getFilters();
//        List<GatewayFilter> combined = new ArrayList<>(this.globalFilters);
//        combined.addAll(gatewayFilters);
//        // TODO: needed or cached?
//         AnnotationAwareOrderComparator.sort(gatewayFilters);

        return new DefaultGatewayFilterChain(gatewayFilters).filter(exchange);
    }

    private static class DefaultGatewayFilterChain implements GatewayFilterChain {

        private final int index;

        @Getter
        private final List<GatewayFilter> filters;

        DefaultGatewayFilterChain(List<GatewayFilter> filters) {
            this.filters = filters;
            this.index = 0;
        }

        private DefaultGatewayFilterChain(DefaultGatewayFilterChain parent, int index) {
            this.filters = parent.getFilters();
            this.index = index;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange) {
            if (this.index < filters.size()) {
                GatewayFilter filter = filters.get(this.index);
                DefaultGatewayFilterChain chain = new DefaultGatewayFilterChain(this, this.index + 1);
                return filter.filter(exchange, chain);
            }
            else {
                return Mono.empty(); // complete
            }
        }

    }
}
