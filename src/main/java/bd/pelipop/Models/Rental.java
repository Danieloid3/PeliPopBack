package bd.pelipop.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "rental")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    @Column(name = "rental_date", nullable = false)
    private Instant rentalDate;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

}