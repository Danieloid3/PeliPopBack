package bd.pelipop.Payload;
import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Models.Gender;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private Gender gender;
    private String country;
    private LocalDate birthdate;
    private TMDBmovieDTO favoriteMovie;

    public AuthResponse(String token, Long userId, String email, String username, Gender gender, String country, LocalDate birthdate, TMDBmovieDTO favoriteMovie) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.gender = gender;
        this.country = country;
        this.birthdate = birthdate;
        this.favoriteMovie = favoriteMovie;
    }
}
