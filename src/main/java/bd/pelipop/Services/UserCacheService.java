package bd.pelipop.Services;

import bd.pelipop.Models.User;
import bd.pelipop.Models.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserCacheService.class);
    private static final String KEY_PREFIX = "user:";

    private final RedisTemplate<String, UserCache> redisTemplate;

    @Value("${app.redis.ttl:86400}")
    private long redisTTL;

    public UserCacheService(RedisTemplate<String, UserCache> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheUser(User user) {
        if (user == null || user.getEmail() == null) {
            logger.warn("Intento de cachear usuario nulo o sin email.");
            return;
        }
        String key = KEY_PREFIX + user.getEmail();
        redisTemplate.opsForValue().set(key, new UserCache(user), redisTTL, TimeUnit.SECONDS);
        logger.info("Usuario cacheado en Redis: {} (ttlSegundos={})", user.getEmail(), redisTTL);
    }

    public UserCache getUserFromCache(String email) {
        if (email == null) return null;
        String key = KEY_PREFIX + email;
        UserCache uc = redisTemplate.opsForValue().get(key);
        if (uc == null) {
            logger.info("MISS de caché para usuario: {}", email);
        } else {
            logger.debug("HIT de caché para usuario: {}", email);
        }
        return uc;
    }

    public void removeFromCache(String email) {
        if (email == null) return;
        String key = KEY_PREFIX + email;
        boolean deleted = Boolean.TRUE.equals(redisTemplate.delete(key));
        if (deleted) {
            logger.info("Usuario removido del caché: {}", email);
        } else {
            logger.debug("No existía entrada para remover: {}", email);
        }
    }
}
