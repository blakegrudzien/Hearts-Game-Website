package heartsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RedisConfig {

    @Value("${REDIS_URL}")
    private String redisUrl;

    @Bean
    public JedisPool jedisPool() throws URISyntaxException {
        URI redisUri = new URI(redisUrl);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        if (redisUri.getScheme().equals("rediss")) {
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
        }

        return new JedisPool(poolConfig, redisUri.getHost(), redisUri.getPort(),
                2000, redisUri.getUserInfo().split(":", 2)[1]);
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(JedisPool jedisPool) throws URISyntaxException {
        URI redisUri = new URI(redisUrl);

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisUri.getHost());
        redisStandaloneConfiguration.setPort(redisUri.getPort());
        redisStandaloneConfiguration.setPassword(redisUri.getUserInfo().split(":", 2)[1]);

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }
}
