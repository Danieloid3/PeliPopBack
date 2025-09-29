package bd.pelipop.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"User\"")
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

    @PrePersist
    public void prePersist() {
        this.createdAt = ZonedDateTime.now().toOffsetDateTime();
    }
}