package heartsapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
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
        // You can configure pool settings here if needed

        if ("rediss".equals(redisUri.getScheme())) {
            // Use SSL/TLS
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLParameters sslParameters = new SSLParameters();
            HostnameVerifier hostnameVerifier = (hostname, session) -> true;
            return new JedisPool(poolConfig, redisUri, sslSocketFactory, sslParameters, hostnameVerifier);
        } else {
            // Standard connection without SSL/TLS
            return new JedisPool(poolConfig, redisUri);
        }
    }

    @Bean
    public Jedis jedis(JedisPool jedisPool) {
        // Retrieve Jedis instance from the pool
        return jedisPool.getResource();
    }


    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}