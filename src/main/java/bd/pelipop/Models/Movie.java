package bd.pelipop.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(name = "country", length = 100)
    private String country;

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