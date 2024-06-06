package io.cc.gateway.route;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author nhsoft.lsd
 */
@Data
@ConfigurationProperties(prefix = "cc.gateway")
public class RouteProperties {

    private List<Route> routes;
}
