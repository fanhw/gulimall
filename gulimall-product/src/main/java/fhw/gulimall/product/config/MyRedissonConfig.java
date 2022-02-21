package fhw.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author fhw
 * @version 1.0
 * @date 2022-02-03 09:29
 */

@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://1.15.106.99:6379");
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}
