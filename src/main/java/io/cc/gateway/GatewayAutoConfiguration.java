package io.cc.gateway;

import cc.rpc.core.api.LoadBalancer;
import cc.rpc.core.api.RegisterCenter;
import cc.rpc.core.config.ConsumerConfig;
import io.cc.config.client.annotation.EnableCcConfig;
import io.cc.gateway.filter.WebClientHttpRoutingFilter;
import io.cc.gateway.handler.FilteringWebHandler;
import io.cc.gateway.handler.PluginWebHandler;
import io.cc.gateway.plugin.WebClientPlugin;
import io.cc.gateway.plugin.CcRpcPlugin;
import io.cc.gateway.route.PathRouteSelector;
import io.cc.gateway.route.RouteProperties;
import io.cc.gateway.route.RouteSelector;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author nhsoft.lsd
 */
@Configuration
@EnableConfigurationProperties(RouteProperties.class)
@Import(ConsumerConfig.class)
@EnableCcConfig
public class GatewayAutoConfiguration {

    @Bean
    public PluginWebHandler pluginWebHandler(List<GatewayPluginFilter> gatewayPluginFilters,
                                             List<GatewayPlugin> gatewayPlugins) {
        return new PluginWebHandler(gatewayPluginFilters, gatewayPlugins);
    }

    @Bean
    public GatewayPlugin inMemoryPlugin(RouteProperties routeProperties, RouteSelector routeSelector){
        return new WebClientPlugin(routeSelector);
    }

    @Bean
    public CcRpcPlugin registryCenterPlugin(RegisterCenter registerCenter, LoadBalancer loadBalancer) {
        return new CcRpcPlugin(registerCenter, loadBalancer);
    }

    @Bean
    @ConditionalOnMissingBean(GatewayPlugin.class)
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
