package heartsapp;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class RedisConfig {

  private static JedisPool pool;

  static {
      getPool();
  }

  private final JedisPool jedisPool;

  public RedisConfig(JedisPool jedisPool) {
      this.jedisPool = jedisPool;
  }

  public JedisPool getJedisPool() {
      return jedisPool;
  }

  public static JedisPool getPool() {
     try {
            // Log the value of REDIS_URL
            String redisUrl = System.getenv("REDIS_URL");
            System.out.println("REDIS_URL: " + redisUrl);

            if (redisUrl == null || redisUrl.isEmpty()) {
                throw new IllegalArgumentException("REDIS_URL environment variable is not set");
            }

            URI redisUri = new URI(redisUrl);

            String password = null;
            if (redisUri.getUserInfo() != null) {
                password = redisUri.getUserInfo().split(":", 2)[1];
            }

            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(10);
            poolConfig.setMaxIdle(5);
            poolConfig.setMinIdle(1);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestOnReturn(true);
            poolConfig.setTestWhileIdle(true);

            return new JedisPool(poolConfig, redisUri.getHost(), redisUri.getPort(), 2000, password);

        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid REDIS_URL: " + e.getMessage(), e);
        }
  }
}
