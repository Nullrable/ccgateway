package io.cc.gateway.route;

import java.net.URI;
import java.util.Map;
import lombok.Data;
import org.springframework.core.Ordered;

/**
 * @author nhsoft.lsd
 */
@Data
public class Route implements Ordered {

    private final String id;

    private final URI uri;

    //可以改造为 predicates
    private final String path;

    private final int order;

    private final Map<String, Object> metadata;
}
