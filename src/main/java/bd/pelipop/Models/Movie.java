package bd.pelipop.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @Column(name = "movie_id", nullable = false)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "director")
    private String director;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    // CAMBIO: Relación ManyToOne
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    private Country country;

    @Column(name = "language", length = 50)
    private String language;

    @Column(name = "synopsis", length = Integer.MAX_VALUE)
    private String synopsis;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "duration")
    private Double duration;

}