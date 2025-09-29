package bd.pelipop.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "moviegenre")
public class Moviegenre {
    @EmbeddedId
    private MoviegenreId id;

    @MapsId("movieId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @MapsId("genreId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

}