package io.cc.gateway.support;

/**
 * @author nhsoft.lsd
 */
public class GatewayExchangeUtils {

    public static final String ROURE = qualify("route");

    private static String qualify(String attr) {
        return GatewayExchangeUtils.class.getName() + "." + attr;
    }
}
