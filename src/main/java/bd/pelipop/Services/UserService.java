package bd.pelipop.Services;

import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Models.Country;
import bd.pelipop.Repositories.CountryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import bd.pelipop.Repositories.UserRepository;
import bd.pelipop.Models.User;
import bd.pelipop.Models.UserCache;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CountryRepository countryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCacheService userCacheService;

    @Autowired
    private TmdbService tmdbService;

    @Autowired
    private ObjectMapper objectMapper;

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

        if (user.getCountryId() != null) {
            Country country = countryRepository.findById(user.getCountryId()).orElse(null);
            user.setCountry(country);
            if (country == null) {
                logger.warn("Se recibió countryId={} pero no se encontró en la BD.", user.getCountryId());
            }
        }

        if (user.getFavoriteMovieId() != null) {
            try {
                TMDBmovieDTO movie = tmdbService.getMovieDetails(user.getFavoriteMovieId());
                user.setFavoriteMovie(objectMapper.writeValueAsString(movie));
            } catch (Exception e) {
                logger.error("No se pudo resolver favoriteMovieId={}, se guardará null", user.getFavoriteMovieId(), e);
                user.setFavoriteMovie(null);
            }
        }

        User saved = userRepository.save(user);
        logger.info("Usuario creado: {}", saved.getEmail());
        return saved;
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        Optional<User> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isEmpty()) {
            logger.warn("Update fallido. Usuario id={} no existe.", id);
            return null;
        }

        User existingUser = existingUserOpt.get();
        String oldEmail = existingUser.getEmail();
        UserCache wasCached = userCacheService.getUserFromCache(oldEmail);

        // CORRECCIÓN: Actualizar solo los campos no nulos para permitir updates parciales
        if (userDetails.getUsername() != null) {
            existingUser.setUsername(userDetails.getUsername());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        if (userDetails.getFullName() != null) {
            existingUser.setFullName(userDetails.getFullName());
        }
        if (userDetails.getGender() != null) {
            existingUser.setGender(userDetails.getGender());
        }
        if (userDetails.getBirthdate() != null) {
            existingUser.setBirthdate(userDetails.getBirthdate());
        }
        if (userDetails.getPhone() != null) {
            existingUser.setPhone(userDetails.getPhone());
        }

        if (userDetails.getCountryId() != null) {
            countryRepository.findById(userDetails.getCountryId())
                    .ifPresent(existingUser::setCountry);
        }

        if (userDetails.getFavoriteMovieId() != null) {
            try {
                TMDBmovieDTO movie = tmdbService.getMovieDetails(userDetails.getFavoriteMovieId());
                existingUser.setFavoriteMovie(objectMapper.writeValueAsString(movie));
            } catch (Exception e) {
                logger.error("No se pudo resolver favoriteMovieId={} en update, se mantiene valor previo",
                        userDetails.getFavoriteMovieId(), e);
            }
        }

        User updated = userRepository.save(existingUser);

        if (wasCached != null && !oldEmail.equals(updated.getEmail())) {
            userCacheService.removeFromCache(oldEmail);
            logger.info("Invalidado caché por actualización de email: oldEmail={} newEmail={}", oldEmail, updated.getEmail());
        }
        // Siempre se actualiza el caché para reflejar los cambios
        userCacheService.cacheUser(updated);

        return updated;
    }

    @Override
    public void deleteUser(Long id) {
        User existing = userRepository.findById(id).orElse(null);
        if (existing != null) {
            userCacheService.removeFromCache(existing.getEmail());
            userRepository.deleteById(id);
            logger.info("Usuario eliminado: {}", existing.getEmail());
        } else {
            logger.warn("Delete solicitado para id={} pero no existe.", id);
        }
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
