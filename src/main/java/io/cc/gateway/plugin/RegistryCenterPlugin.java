package io.cc.gateway.plugin;

import cc.rpc.core.api.RegisterCenter;
import io.cc.gateway.AbstractGatewayPlugin;
import io.cc.gateway.GatewayPluginChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public class RegistryCenterPlugin extends AbstractGatewayPlugin {

    private RegisterCenter registerCenter;

    @Override
    public boolean doSupports(final ServerWebExchange exchange) {

//        List<InstanceMeta> instanceMetas = registerCenter.fetchAll(serviceMeta);

        return super.doSupports(exchange);
    }

    @Override
    public Mono<Void> doHandle(final ServerWebExchange exchange, final GatewayPluginChain chain) {
        return super.doHandle(exchange, chain);
    }
}
