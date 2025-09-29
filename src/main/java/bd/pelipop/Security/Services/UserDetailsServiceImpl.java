package bd.pelipop.Security.Services;

import bd.pelipop.Repositories.UserRepository;
import bd.pelipop.Services.UserCacheService;
import bd.pelipop.Models.User;
import bd.pelipop.Models.UserCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheService userCacheService;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Intento en caché
        UserCache cached = userCacheService.getUserFromCache(email);
        if (cached != null) {
            logger.info("Autenticación usando usuario del caché: {}", email);
            return new org.springframework.security.core.userdetails.User(
                    cached.getEmail(),
                    cached.getPasswordHash(),
                    new ArrayList<>()
            );
        }

        // 2. Miss -> BD
        logger.info("Primer login o caché expirado para: {}. Consultando BD.", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado en BD tras miss de caché: {}", email);
                    return new UsernameNotFoundException("User Not Found with email: " + email);
                });

        // 3. Cachear tras autenticación
        userCacheService.cacheUser(user);
        logger.info("Usuario {} agregado al caché tras autenticación.", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                new ArrayList<>()
        );
    }
}
