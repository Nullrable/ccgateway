package io.cc.gateway.handler;

import io.cc.gateway.GatewayPlugin;
import io.cc.gateway.GatewayPluginChain;
import io.cc.gateway.GatewayPluginFilter;
import java.util.List;
import lombok.Getter;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public class PluginWebHandler implements WebHandler {

    public static final String NAME = "pluginWebHandler";

    private final List<GatewayPluginFilter> gatewayPluginFilters;
    private final List<GatewayPlugin> gatewayPlugins;

    public PluginWebHandler(final List<GatewayPluginFilter> gatewayPluginFilters,
                            final List<GatewayPlugin> gatewayPlugins) {
        this.gatewayPluginFilters = gatewayPluginFilters;
        this.gatewayPlugins = gatewayPlugins;
    }

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange) {

        if (gatewayPluginFilters != null) {
            for (GatewayPluginFilter gatewayPluginFilter : gatewayPluginFilters) {
                gatewayPluginFilter.filter(exchange);
            }
        }

        return new DefaultGatewayPluginChain(gatewayPlugins).handle(exchange);
    }

    private static class DefaultGatewayPluginChain implements GatewayPluginChain {

        private int index;

        @Getter
        private final List<GatewayPlugin> plugins;

        DefaultGatewayPluginChain(List<GatewayPlugin> plugins) {
            this.plugins = plugins;
            this.index = 0;
        }

        @Override
        public Mono<Void> handle(ServerWebExchange exchange) {
            return Mono.defer(() -> {
                if (this.index < plugins.size()) {
                    GatewayPlugin plugin = plugins.get(index++);
                    return plugin.handle(exchange, this);
                }
                else {
                    return Mono.empty(); // complete
                }
            });
        }
    }
}
