package bd.pelipop.Models;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String username;
    private String passwordHash;
    private String role;

    // Constructor para convertir desde el modelo User
    public UserCache(bd.pelipop.Models.User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
    }
}