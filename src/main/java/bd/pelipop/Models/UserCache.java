package bd.pelipop.Models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import bd.pelipop.DTO.TMDBmovieDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private String gender;
    private String country;
    private LocalDate birthdate;
    private TMDBmovieDTO favoriteMovie;

    // Constructor para convertir desde el modelo User
    public UserCache(bd.pelipop.Models.User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.gender = user.getGender();
        this.country = user.getCountry();
        this.birthdate = user.getBirthdate();

        try {
            if (user.getFavoriteMovie() != null) {
                ObjectMapper mapper = new ObjectMapper();
                this.favoriteMovie = mapper.readValue(user.getFavoriteMovie(), TMDBmovieDTO.class);
            }
        }
        catch (Exception e) {
            this.favoriteMovie = null; // fallback si el JSON es inválido
        }
    }

}