package heartsapp;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
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
      if (pool == null) {
          try {
              TrustManager bogusTrustManager = new X509TrustManager() {
                  public X509Certificate[] getAcceptedIssuers() {
                      return null;
                  }

                  public void checkClientTrusted(X509Certificate[] certs, String authType) {
                  }

                  public void checkServerTrusted(X509Certificate[] certs, String authType) {
                  }
              };

              SSLContext sslContext = SSLContext.getInstance("SSL");
              sslContext.init(null, new TrustManager[]{bogusTrustManager}, new java.security.SecureRandom());

              HostnameVerifier bogusHostnameVerifier = (hostname, session) -> true;

              JedisPoolConfig poolConfig = new JedisPoolConfig();
              poolConfig.setMaxTotal(10);
              poolConfig.setMaxIdle(5);
              poolConfig.setMinIdle(1);
              poolConfig.setTestOnBorrow(true);
              poolConfig.setTestOnReturn(true);
              poolConfig.setTestWhileIdle(true);

              pool = new JedisPool(poolConfig,
                      URI.create(System.getenv("REDIS_URL")),
                      sslContext.getSocketFactory(),
                      sslContext.getDefaultSSLParameters(),
                      bogusHostnameVerifier);

          } catch (NoSuchAlgorithmException | KeyManagementException e) {
              throw new RuntimeException("Cannot obtain Redis connection!", e);
          }
      }
      return pool;
  }
}