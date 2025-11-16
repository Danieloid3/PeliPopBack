package bd.pelipop.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long id;

    @Column(unique = true, name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = Integer.MAX_VALUE)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone", length = 20, unique = true)
    private String phone;

    @Column(name = "email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "role", nullable = false, length = 20)
    private String role;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "favorite_movie", columnDefinition = "jsonb")
    private String favoriteMovie;

    // Solo para recibir el ID TMDB desde el request; no se persiste
    @Transient
    private Long favoriteMovieId;
    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now().toOffsetDateTime();

    }

}