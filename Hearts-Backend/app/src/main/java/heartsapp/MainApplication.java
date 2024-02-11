package heartsapp;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class MainApplication {

    public static void main(String[] args) {
        System.out.println("Started main application");

        // Create Jedis pool
        JedisPool jedisPool = RedisConfig.getPool();

        // Use jedisPool to get a Jedis instance
        try (Jedis jedis = jedisPool.getResource()) {
            // Now you can use the jedis instance to interact with Redis
            jedis.set("key", "value");
            String value = jedis.get("key");
            System.out.println("Retrieved value from Redis: " + value);
        }
    }
}
