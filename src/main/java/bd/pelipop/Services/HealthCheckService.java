package bd.pelipop.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HealthCheckService {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate redisTemplate;

    @Autowired
    public HealthCheckService(JdbcTemplate jdbcTemplate, StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    public Map<String, String> check() {
        Map<String, String> result = new HashMap<>();

        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            result.put("db", "UP");
        } catch (Exception e) {
            result.put("db", "DOWN");
        }

        try {
            RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
            if (factory == null) {
                throw new IllegalStateException("Redis connection factory is null");
            }
            try (var connection = factory.getConnection()) {
                connection.ping();
            }
            result.put("redis", "UP");
        } catch (Exception e) {
            result.put("redis", "DOWN");
        }

        if ("UP".equals(result.get("db")) && "UP".equals(result.get("redis"))) {
            result.put("status", "UP");
        } else {
            result.put("status", "DOWN");
        }

        return result;
    }
}