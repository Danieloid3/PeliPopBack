package bd.pelipop.Config;

import bd.pelipop.Models.UserCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserCache> userCacheRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserCache> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<UserCache> valueSerializer =
                new Jackson2JsonRedisSerializer<>(UserCache.class);
        valueSerializer.setObjectMapper(mapper);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
