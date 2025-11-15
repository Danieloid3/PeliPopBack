// src/main/java/bd/pelipop/Controllers/UserController.java
package bd.pelipop.Controllers;

import bd.pelipop.Models.UserCache;
import bd.pelipop.Services.UserCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import bd.pelipop.Models.User;
import bd.pelipop.Security.JWT.JwtUtil;
import bd.pelipop.Services.IUserService;
import bd.pelipop.Payload.LoginRequest;
import bd.pelipop.Payload.AuthResponse;

import java.util.List;

@RestController
@RequestMapping("/pelipop")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService iUserService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    private UserCacheService userCacheService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        logger.info("Intentando login para email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateJwtToken(authentication);

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String email = principal.getUsername();

        UserCache cached = userCacheService.getUserFromCache(email);

        if (cached == null) {
            // Caso raro: expiró entre autenticación y aquí, o fallo de cache.
            logger.warn("Usuario {} no encontrado en caché justo después de autenticación. Fallback a BD.", email);
            User user = iUserService.findUserByEmail(email);
            if (user == null) {
                logger.error("Usuario {} no recuperable tras autenticación exitosa.", email);
                return ResponseEntity.internalServerError().body("No se pudo recuperar el usuario.");
            }
            userCacheService.cacheUser(user);
            cached = new UserCache(user);
        } else {
            logger.debug("Login usando entrada de caché para {}", email);
        }

        logger.info("Usuario {} autenticado correctamente.", email);
        AuthResponse response = new AuthResponse(jwt, cached.getId(), cached.getEmail(), cached.getUsername());
        return ResponseEntity.ok(response);
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
