package io.cc.gateway.plugin;

import io.cc.gateway.AbstractGatewayPlugin;
import io.cc.gateway.GatewayPluginChain;
import io.cc.gateway.route.PathRouteSelector;
import io.cc.gateway.route.Route;
import io.cc.gateway.support.GatewayExchangeUtils;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author nhsoft.lsd
 */
public class InMemoryPlugin extends AbstractGatewayPlugin {

    private final List<Route> routes;

    private PathRouteSelector routeSelector;

    public InMemoryPlugin(final List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public Mono<Void> doHandle(final ServerWebExchange exchange, final GatewayPluginChain chain) {
        Route route = exchange.getRequiredAttribute(GatewayExchangeUtils.ROURE);
        URI requestUrl = route.getUri();
        String scheme = requestUrl.getScheme();

        if ((!"http".equals(scheme) && !"https".equals(scheme))) {
            return chain.handle(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();

        HttpMethod method = request.getMethod();

        WebClient.RequestBodySpec bodySpec = WebClient.create().method(method).uri(requestUrl + toPath(request)).headers(httpHeaders -> {
            httpHeaders.addAll(exchange.getRequest().getHeaders());
        });

        WebClient.RequestHeadersSpec<?> headersSpec;
        if (requiresBody(method)) {
            headersSpec = bodySpec.body(BodyInserters.fromDataBuffers(request.getBody()));
        }
        else {
            headersSpec = bodySpec;
        }

        return headersSpec.exchangeToMono(resp -> {
            Mono<ResponseEntity<String>> entity = resp.toEntity(String.class);

            Mono<String> body = entity.mapNotNull(ResponseEntity::getBody);
            exchange.getResponse().getHeaders().addAll(resp.request().getHeaders());

            return body.flatMap(x->exchange.getResponse()
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
        if (routes == null || routes.isEmpty()) {
            return false;
        }
        Route route = routeSelector.selectOne(exchange);
        return route != null;
    }

    private boolean requiresBody(HttpMethod method) {
        if (method.equals(HttpMethod.PUT) || method.equals(HttpMethod.POST) || method.equals(HttpMethod.PATCH)) {
            return true;
        }
        return false;
    }
}
