package bd.pelipop.Services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import bd.pelipop.Repositories.UserRepository;
import bd.pelipop.Models.User;
import bd.pelipop.Models.UserCache;

import java.util.List;

@Service
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCacheService userCacheService;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User createUser(User user) {
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        User saved = userRepository.save(user);
        logger.info("Usuario creado (no cacheado todavía hasta login): {}", saved.getEmail());
        return saved;
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            logger.warn("Update fallido. Usuario id={} no existe.", id);
            return null;
        }

        String oldEmail = existingUser.getEmail();
        UserCache wasCached = userCacheService.getUserFromCache(oldEmail);

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        existingUser.setFullName(userDetails.getFullName());

        User updated = userRepository.save(existingUser);

        if (wasCached != null) {
            userCacheService.removeFromCache(oldEmail);
            logger.info("Invalidado caché por actualización de usuario: oldEmail={} newEmail={}", oldEmail, updated.getEmail());
        } else {
            logger.debug("Usuario actualizado sin entrada previa en caché: {}", oldEmail);
        }

        return updated;
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            userCacheService.removeFromCache(existing.getEmail());
            userRepository.deleteById(id);
            logger.info("Usuario eliminado (y caché invalidado si existía): {}", existing.getEmail());
        } else {
            logger.warn("Delete solicitado para id={} pero no existe.", id);
        }
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
