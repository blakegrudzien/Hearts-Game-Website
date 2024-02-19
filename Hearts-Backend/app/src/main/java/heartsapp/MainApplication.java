package heartsapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootApplication
@ComponentScan(basePackages = "heartsapp")
public class MainApplication {

    public static void main(String[] args) {
        // Start the Spring application context
        ConfigurableApplicationContext context = SpringApplication.run(MainApplication.class, args);

        // Retrieve the RedisTemplate bean from the context
        RedisTemplate<String, String> redisTemplate = context.getBean(RedisTemplate.class);

        // Use redisTemplate to interact with Redis
        redisTemplate.opsForValue().set("key", "value");
        String value = redisTemplate.opsForValue().get("key");
        System.out.println("Retrieved value from Redis: " + value);

        // Close the application context
        context.close();
    }
}