package io.cc.gateway.plugin;

import io.cc.gateway.AbstractGatewayPlugin;
import io.cc.gateway.GatewayPluginChain;
import io.cc.gateway.route.Route;
import io.cc.gateway.route.RouteSelector;
import io.cc.gateway.support.PluginEnum;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
@Slf4j
public class WebClientPlugin extends AbstractGatewayPlugin {

    private RouteSelector routeSelector;

    public WebClientPlugin(final RouteSelector routeSelector) {
        this.routeSelector = routeSelector;
    }

    @Override
    public Mono<Void> doHandle(final ServerWebExchange exchange, final GatewayPluginChain chain) {

        Route route = routeSelector.selectOne(exchange);
        URI requestUrl = route.getUri();
        String scheme = requestUrl.getScheme();

        if ((!"http".equals(scheme) && !"https".equals(scheme))) {
            return chain.handle(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        HttpMethod method = request.getMethod();

        WebClient.RequestBodySpec bodySpec = WebClient.create().method(method).uri(requestUrl + toPath(request)).headers(httpHeaders -> {
            httpHeaders.addAll(exchange.getRequest().getHeaders());
            httpHeaders.remove(HttpHeaders.HOST);
        });

        WebClient.RequestHeadersSpec<?> headersSpec;
        if (requiresBody(method)) {
            headersSpec = bodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
        } else {
            headersSpec = bodySpec;
        }

        return headersSpec.exchangeToMono(resp -> {
            Mono<ResponseEntity<String>> entity = resp.toEntity(String.class);

            Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);
            exchange.getResponse().getHeaders().addAll(resp.request().getHeaders());

            return body.flatMap(x-> exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(x.getBytes()))))
                    .then(chain.handle(exchange));
        });
    }

    private String toPath(ServerHttpRequest request) {
        String path = request.getPath().value();
        String query = request.getURI().getQuery();

        if (query != null && !query.isEmpty()) {
            return path + "?" + query;
        }
        return path;
    }

    @Override
    public boolean doSupports(final ServerWebExchange exchange) {
        String plugin = exchange.getRequest().getHeaders().getFirst("plugin");
        return plugin != null && plugin.equals(PluginEnum.WEB_CLIENT.name());
    }

    private boolean requiresBody(HttpMethod method) {
        if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            return true;
        }
        return false;
    }
}
