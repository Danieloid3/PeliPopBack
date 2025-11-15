package bd.pelipop.DTO;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Usamos @Data de Lombok para generar getters, setters, toString, etc.
@Data
public class TMDBmovieDTO {

    private long id;
    private String title;

    // La API de TMDB devuelve "overview", lo mapeamos a "synopsis".
    @JsonProperty("overview")
    private String synopsis;

    // La API devuelve "release_date", lo mapeamos a nuestro campo.
    @JsonProperty("release_date")
    private String releaseDate;

    // La API devuelve la ruta parcial, no la URL completa.
    @JsonProperty("poster_path")
    private String posterPath;

    @JsonProperty("vote_average")
    private double voteAverage;
}
