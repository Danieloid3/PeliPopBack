package bd.pelipop.Repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import bd.pelipop.Models.User;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
