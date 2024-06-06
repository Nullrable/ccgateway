package io.cc.gateway;

import io.cc.gateway.filter.WebClientHttpRoutingFilter;
import io.cc.gateway.handler.FilteringWebHandler;
import io.cc.gateway.route.PathRouteSelector;
import io.cc.gateway.route.RouteProperties;
import io.cc.gateway.route.RouteSelector;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author nhsoft.lsd
 */
@Configuration
@EnableConfigurationProperties(RouteProperties.class)
public class GatewayAutoConfiguration {

    @Bean
    public WebClientHttpRoutingFilter webClientHttpRoutingFilter() {
        return new WebClientHttpRoutingFilter();
    }

    @Bean(FilteringWebHandler.NAME)
    public FilteringWebHandler filteringWebHandler(List<GatewayFilter> gatewayFilters, RouteSelector routeSelector) {
        return new FilteringWebHandler(gatewayFilters, routeSelector);
    }

    @Bean
    public PathRouteSelector routeSelector(RouteProperties routeProperties){
        return new PathRouteSelector(routeProperties.getRoutes());
    }
}
