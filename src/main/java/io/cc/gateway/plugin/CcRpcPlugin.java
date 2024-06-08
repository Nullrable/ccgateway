package io.cc.gateway.plugin;

import cc.rpc.core.api.LoadBalancer;
import cc.rpc.core.api.RegisterCenter;
import cc.rpc.core.meta.InstanceMeta;
import cc.rpc.core.meta.ServiceMeta;
import io.cc.gateway.AbstractGatewayPlugin;
import io.cc.gateway.GatewayPluginChain;
import io.cc.gateway.support.PluginEnum;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public class CcRpcPlugin extends AbstractGatewayPlugin {

    private RegisterCenter registerCenter;

    private LoadBalancer loadBalancer;

    public CcRpcPlugin(final RegisterCenter registerCenter, final LoadBalancer loadBalancer) {
        this.registerCenter = registerCenter;
        this.loadBalancer = loadBalancer;
    }

    @Override
    public boolean doSupports(final ServerWebExchange exchange) {
        String plugin = exchange.getRequest().getHeaders().getFirst("plugin");
        return plugin != null && plugin.equals(PluginEnum.CCRPC.name());
    }

    @Override
    public Mono<Void> doHandle(final ServerWebExchange exchange, final GatewayPluginChain chain) {

        String service = exchange.getRequest().getHeaders().getFirst("service");
        String app = exchange.getRequest().getHeaders().getFirst("app");
        String namespace = exchange.getRequest().getHeaders().getFirst("namespace");
        String env = exchange.getRequest().getHeaders().getFirst("env");

        ServiceMeta serviceMeta = ServiceMeta.builder().app(app).env(env).namespace(namespace).service(service).build();

        List<InstanceMeta> instanceMetas = registerCenter.fetchAll(serviceMeta);
        InstanceMeta instanceMeta = loadBalancer.choose(instanceMetas);

        String requestUrl = instanceMeta.toUrl();

        System.out.println("=====>>>>>" + requestUrl);

        WebClient.RequestHeadersSpec<?> headersSpec = WebClient.create().method(HttpMethod.POST).uri(requestUrl)
                .headers(headers -> {
                    headers.addAll(exchange.getRequest().getHeaders());
                    headers.remove(HttpHeaders.HOST);
                })
                .body(BodyInserters.fromDataBuffers(exchange.getRequest().getBody()));

        return headersSpec.exchangeToMono(resp -> {
            Mono<ResponseEntity<String>> entity = resp.toEntity(String.class);

            Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);
            exchange.getResponse().getHeaders().addAll(resp.request().getHeaders());

            return body.flatMap(x-> exchange.getResponse()
                            .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                    .then(chain.handle(exchange));
        });

    }
}
