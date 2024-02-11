package heartsapp;
import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.url}")
    private String redisUrl;

    @Bean
public Jedis jedis() {
    return new Jedis(URI.create(redisUrl));
}
}