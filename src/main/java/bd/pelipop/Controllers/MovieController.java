package bd.pelipop.Controllers;


import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Services.TmdbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
