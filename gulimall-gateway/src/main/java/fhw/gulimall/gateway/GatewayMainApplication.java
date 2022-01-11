package fhw.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;


/**
 * @author fhw
 * @version 1.0
 * @date 2022-01-11 17:04
 */

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@RefreshScope
public class GatewayMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayMainApplication.class, args);
    }
}
