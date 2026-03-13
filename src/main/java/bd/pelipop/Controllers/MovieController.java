package bd.pelipop.Controllers;


import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Payload.TmdbAddMovieToListRequest;
import bd.pelipop.Payload.TmdbCreateListRequest;
import bd.pelipop.Payload.TmdbRateMovieRequest;
import bd.pelipop.Services.TmdbService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pelipop/movies")
public class MovieController {

    @Autowired
    private TmdbService tmdbService;

    /**
     * Endpoint para obtener las películas más populares.
     * URL: GET http://localhost:5000/pelipop/movies/popular
     */
    @GetMapping("/popular")
    public ResponseEntity<List<TMDBmovieDTO>> getPopularMovies() {
        List<TMDBmovieDTO> movies = tmdbService.getPopularMovies();
        return ResponseEntity.ok(movies);
    }

    /**
     * Endpoint para buscar películas por un término.
     * URL: GET http://localhost:5000/pelipop/movies/search?query=termino_a_buscar
     * @param query El término de búsqueda pasado como parámetro en la URL.
     */
    @GetMapping("/search")
    public ResponseEntity<List<TMDBmovieDTO>> searchMovies(@RequestParam String query) {
        List<TMDBmovieDTO> movies = tmdbService.searchMovies(query);
        return ResponseEntity.ok(movies);
    }
    /**
     * Endpoint para obtener los detalles de una película por su ID de TMDB.
     * URL: GET http://localhost:5000/pelipop/movies/details/12345
     * @param id El ID de la película.
     */
    @GetMapping("/details/{id}")
    public ResponseEntity<TMDBmovieDTO> getMovieDetails(@PathVariable long id) {
        TMDBmovieDTO movieDetails = tmdbService.getMovieDetails(id);
        return ResponseEntity.ok(movieDetails);
    }

    /**
     * Endpoint para buscar películas por género usando TMDB Discover.
     * Ejemplo: GET http://localhost:5000/pelipop/movies/by-genre?genreId=28&page=1&language=es-ES
     */
    @GetMapping("/by-genre")
    public ResponseEntity<List<TMDBmovieDTO>> getMoviesByGenre(@RequestParam String genreId,
                                                                @RequestParam(required = false) Integer page,
                                                                @RequestParam(required = false) String language) {
        List<TMDBmovieDTO> movies = tmdbService.discoverMoviesByGenre(genreId, page, language);
        return ResponseEntity.ok(movies);
    }

    /**
     * Endpoint para obtener películas similares a una película por su ID de TMDB.
     * Ejemplo: GET http://localhost:5000/pelipop/movies/12345/similar?page=1&language=es-ES
     */
    @GetMapping("/{id}/similar")
    public ResponseEntity<List<TMDBmovieDTO>> getSimilarMovies(@PathVariable long id,
                                                                @RequestParam(required = false) Integer page,
                                                                @RequestParam(required = false) String language) {
        List<TMDBmovieDTO> movies = tmdbService.getSimilarMovies(id, page, language);
        return ResponseEntity.ok(movies);
    }

    @PostMapping("/lists")
    public ResponseEntity<Map<String, Object>> createList(@Valid @RequestBody TmdbCreateListRequest request) {
        Map<String, Object> response = tmdbService.createList(
                request.getSessionId(),
                request.getName(),
                request.getDescription(),
                request.getLanguage()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/lists/{listId}/items")
    public ResponseEntity<Map<String, Object>> addMovieToList(@PathVariable long listId,
                                                               @Valid @RequestBody TmdbAddMovieToListRequest request) {
        Map<String, Object> response = tmdbService.addMovieToList(request.getSessionId(), listId, request.getMediaId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/lists/{listId}/items/{mediaId}")
    public ResponseEntity<Map<String, Object>> removeMovieFromList(@PathVariable long listId,
                                                                    @PathVariable long mediaId,
                                                                    @RequestParam String sessionId) {
        Map<String, Object> response = tmdbService.removeMovieFromList(sessionId, listId, mediaId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ratings")
    public ResponseEntity<Map<String, Object>> rateMovie(@Valid @RequestBody TmdbRateMovieRequest request) {
        Map<String, Object> response = tmdbService.rateMovie(request.getSessionId(), request.getMovieId(), request.getValue());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{movieId}/ratings")
    public ResponseEntity<Map<String, Object>> deleteMovieRating(@PathVariable long movieId,
                                                                  @RequestParam String sessionId) {
        Map<String, Object> response = tmdbService.deleteMovieRating(sessionId, movieId);
        return ResponseEntity.ok(response);
    }
}
