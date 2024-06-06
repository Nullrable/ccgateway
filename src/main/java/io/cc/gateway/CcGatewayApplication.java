package io.cc.gateway;

import cc.rpc.core.config.ConsumerConfig;
import io.cc.config.client.annotation.EnableCcConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
//@Import(ConsumerConfig.class)
//@EnableCcConfig
public class CcGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(CcGatewayApplication.class, args);
    }
}
