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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserCacheService userCacheService;

    private List<GrantedAuthority> buildAuthorities(String role) {
        if (role == null || role.isBlank()) {
            return List.of();
        }
        // En la BD guardas "ADMIN" -> Spring espera "ROLE_ADMIN"
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserCache cached = userCacheService.getUserFromCache(email);
        if (cached != null) {
            logger.info("Autenticación caché: {}", email);
            return new org.springframework.security.core.userdetails.User(
                    cached.getEmail(),
                    cached.getPasswordHash(),
                    buildAuthorities(cached.getRole())
            );
        }

        logger.info("Miss caché, BD: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        userCacheService.cacheUser(user);
        logger.info("Usuario cacheado tras login: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                buildAuthorities(user.getRole())
        );
    }
}
