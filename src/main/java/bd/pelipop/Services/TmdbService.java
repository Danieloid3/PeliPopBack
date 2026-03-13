package bd.pelipop.Services;



import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.DTO.TMDBmovieResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private String resolveLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "es-ES";
        }
        return language;
    }

    private int resolvePage(Integer page) {
        if (page == null || page < 1) {
            return 1;
        }
        return Math.min(page, 500);
    }

    /**
     * Busca películas usando el endpoint discover de TMDB filtrando por género.
     * El parámetro genreId debe ser un ID de género TMDB (o varios separados por coma).
     */
    public List<TMDBmovieDTO> discoverMoviesByGenre(String genreId, Integer page, String language) {
        int resolvedPage = resolvePage(page);
        String resolvedLanguage = resolveLanguage(language);

        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/discover/movie")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("with_genres", genreId)
                .queryParam("language", resolvedLanguage)
                .queryParam("page", resolvedPage)
                .toUriString();

        TMDBmovieResponse response = restTemplate.getForObject(url, TMDBmovieResponse.class);
        return response != null ? response.getResults() : Collections.emptyList();
    }

    /**
     * Obtiene películas similares a una película dada por su ID de TMDB.
     */
    public List<TMDBmovieDTO> getSimilarMovies(long movieId, Integer page, String language) {
        int resolvedPage = resolvePage(page);
        String resolvedLanguage = resolveLanguage(language);

        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/movie/" + movieId + "/similar")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", resolvedLanguage)
                .queryParam("page", resolvedPage)
                .toUriString();

        TMDBmovieResponse response = restTemplate.getForObject(url, TMDBmovieResponse.class);
        return response != null ? response.getResults() : Collections.emptyList();
    }

    private Map<String, Object> postForMap(String url, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyMap();
    }

    private Map<String, Object> deleteForMap(String url) {
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.DELETE, null, Map.class);
        return response.getBody() != null ? response.getBody() : Collections.emptyMap();
    }

    public Map<String, Object> createList(String sessionId, String name, String description, String language) {
        String resolvedLanguage = resolveLanguage(language);

        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/list")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("session_id", sessionId)
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("name", name);
        body.put("description", description != null ? description : "");
        body.put("language", resolvedLanguage);

        return postForMap(url, body);
    }

    public Map<String, Object> addMovieToList(String sessionId, long listId, long mediaId) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/list/" + listId + "/add_item")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("session_id", sessionId)
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("media_id", mediaId);

        return postForMap(url, body);
    }

    public Map<String, Object> removeMovieFromList(String sessionId, long listId, long mediaId) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/list/" + listId + "/remove_item")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("session_id", sessionId)
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("media_id", mediaId);

        return postForMap(url, body);
    }

    public Map<String, Object> rateMovie(String sessionId, long movieId, double value) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/movie/" + movieId + "/rating")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("session_id", sessionId)
                .toUriString();

        Map<String, Object> body = new HashMap<>();
        body.put("value", value);

        return postForMap(url, body);
    }

    public Map<String, Object> deleteMovieRating(String sessionId, long movieId) {
        String url = UriComponentsBuilder.fromHttpUrl(tmdbApiUrl + "/movie/" + movieId + "/rating")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("session_id", sessionId)
                .toUriString();

        return deleteForMap(url);
    }
}
