package heartsapp;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class RedisConfig {

    private static final JedisPool jedisPool;

    static {
        jedisPool = createJedisPool();
    }

    public static JedisPool getPool() {
        return jedisPool;
    }

    private static JedisPool createJedisPool() {
        String redisUrl = System.getenv("REDIS_URL");
        if (redisUrl == null || redisUrl.isEmpty()) {
            throw new IllegalArgumentException("REDIS_URL environment variable is not set");
        }

        URI redisUri = URI.create(redisUrl);
        String password = redisUri.getUserInfo().split(":", 2)[1];

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        // Configure SSL context to trust all certificates (use this cautiously in production)
        SSLContext sslContext;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, null, null);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create SSL context", e);
        }

        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        // Configure Jedis pool with SSL socket factory
        return new JedisPool(poolConfig, redisUri.getHost(), redisUri.getPort(), 2000, password, redisUri.getScheme().equals("rediss"), sslSocketFactory, null, null);
    }
}
