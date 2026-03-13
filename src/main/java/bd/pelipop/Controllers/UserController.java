package bd.pelipop.Controllers;

import bd.pelipop.Models.Country;
import bd.pelipop.Models.Gender;
import bd.pelipop.Repositories.CountryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import bd.pelipop.Models.User;
import bd.pelipop.Services.IUserService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pelipop")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    private CountryRepository countryRepository;

    @GetMapping("/countries")
    public List<Country> getAllCountries() {
        logger.info("Solicitando lista de países.");
        return countryRepository.findAll();
    }

    @GetMapping("/genders")
    public List<String> getAllGenders() {
        logger.info("Solicitando lista de géneros.");
        return Arrays.stream(Gender.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @PostMapping("/users/save")
    public User createUser(@RequestBody User user) {
        logger.info("Creando usuario: {}", user.getEmail());
        User createdUser = iUserService.createUser(user);
        logger.info("Usuario creado con ID: {}", createdUser.getId());
        createdUser.setPasswordHash(null);
        return createdUser;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        logger.info("Listado de usuarios");
        List<User> users = iUserService.getAllUsers();
        users.forEach(u -> u.setPasswordHash(null));
        if (users.isEmpty()) {
            logger.warn("No hay usuarios.");
        }
        return users;
    }

    @GetMapping("/users/id/{id}")
    public User getUserById(@PathVariable Long id) {
        logger.info("Buscando usuario ID={}", id);
        User user = iUserService.getUserById(id);
        if (user == null) {
            logger.warn("No encontrado ID={}", id);
            return null;
        }
        user.setPasswordHash(null);
        return user;
    }

    @GetMapping("/users/email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        logger.info("Buscando usuario email={}", email);
        User user = iUserService.findUserByEmail(email);
        if (user == null) {
            logger.warn("No encontrado email={}", email);
            return null;
        }
        user.setPasswordHash(null);
        return user;
    }

    @PutMapping("/users/update/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        logger.info("Actualizando usuario ID={}", id);
        User updatedUser = iUserService.updateUser(id, user);
        if (updatedUser == null) {
            logger.warn("No existe usuario ID={} para actualizar", id);
            return null;
        }
        updatedUser.setPasswordHash(null);
        return updatedUser;
    }

    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info("Eliminando usuario ID={}", id);
        User user = iUserService.getUserById(id);
        if (user == null) {
            logger.warn("No existe usuario ID={} para borrar", id);
            return ResponseEntity.notFound().build();
        }
        iUserService.deleteUser(id);
        return ResponseEntity.ok("Usuario eliminado.");
    }

    @GetMapping("/users/name/{name}")
    public User findUserByName(@PathVariable String name) {
        logger.info("Buscando usuario por nombre={}", name);
        User user = iUserService.findUserByUsername(name);
        if (user == null) {
            logger.warn("No encontrado nombre={}", name);
            return null;
        }
        user.setPasswordHash(null);
        return user;
    }
}
