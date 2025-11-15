package bd.pelipop.Services;



import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.DTO.TMDBmovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Service
public class TmdbService {

    // Inyectamos los valores desde application.properties
    @Value("${tmdb.api.url}")
    private String tmdbApiUrl;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;

    private final RestTemplate restTemplate;

    public TmdbService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Obtiene una lista de las películas más populares desde TMDB.
     * @return Una lista de objetos TmdbMovieDTO.
     */
    public List<TMDBmovieDTO> getPopularMovies() {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/movie/popular")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "es-ES") // Opcional: para obtener resultados en español
                .toUriString();

        TMDBmovieResponse response = restTemplate.getForObject(url, TMDBmovieResponse.class);

        return response != null ? response.getResults() : Collections.emptyList();
    }

    /**
     * Busca películas por un título o palabra clave.
     * @param query El término de búsqueda.
     * @return Una lista de películas que coinciden con la búsqueda.
     */
    public List<TMDBmovieDTO> searchMovies(String query) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/search/movie")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("query", query)
                .queryParam("language", "es-ES")
                .toUriString();

        TMDBmovieResponse response = restTemplate.getForObject(url, TMDBmovieResponse.class);

        return response != null ? response.getResults() : Collections.emptyList();
    }


    /**
     * Obtiene los detalles completos de una película específica desde TMDB.
     * @param movieId El ID de la película en TMDB.
     * @return Un objeto TmdbMovieDTO con los detalles de la película.
     */
    public TMDBmovieDTO getMovieDetails(long movieId) {
        // Nota: Para obtener el director, se necesita una llamada adicional a /movie/{id}/credits
        // Para simplificar, este ejemplo se centra en los datos del endpoint principal de detalles.
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/movie/" + movieId)
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "es-ES")
                .toUriString();

        // La respuesta aquí es un solo objeto de película, no una lista.
        TMDBmovieDTO movieDetails = restTemplate.getForObject(url, TMDBmovieDTO.class);

        // Aquí podrías hacer la llamada a /credits para obtener el director y añadirlo al DTO si lo necesitas.
        // Por ahora, devolvemos los detalles que ya tenemos.

        return movieDetails;
    }
}


