package bd.pelipop.Services;

import bd.pelipop.DTO.FavoriteMovieStat;
import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Models.Gender;
import bd.pelipop.Models.User;
import bd.pelipop.Repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

@Service
public class AnalyticsETLService {

    private final MongoClient mongoClient;
    private final String dbName;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public AnalyticsETLService(MongoClient mongoClient,
                               @Value("${app.mongo.db}") String dbName,
                               ObjectMapper objectMapper,
                               UserRepository userRepository) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }

    private MongoCollection<Document> analyticsSummary() {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db.getCollection("analytics_summary");
    }

    @Scheduled(cron = "${app.analytics.cron}")
    public void generateAndStoreAnalytics() {
        // 1. EXTRACT: Obtener todos los datos de la base de datos relacional
        List<User> users = userRepository.findAll();

        // 2. TRANSFORM: Calcular las estadísticas en Java
        long totalUsers = users.size();

        Map<String, Long> byGender = users.stream()
                .map(User::getGender)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(Gender::name, Collectors.counting()));

        // CORRECCIÓN: Usar el nombre del país para la agrupación.
        Map<String, Long> byCountry = users.stream()
                .map(u -> u.getCountry() != null ? u.getCountry().getName() : "Desconocido")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        double averageAge = users.stream()
                .map(User::getBirthdate)
                .filter(Objects::nonNull)
                .mapToDouble(birthdate -> Period.between(birthdate, LocalDate.now()).getYears())
                .average()
                .orElse(0.0);

        List<FavoriteMovieStat> topFavoriteMovies = users.stream()
                .map(user -> {
                    try {
                        return user.getFavoriteMovie() != null ? objectMapper.readValue(user.getFavoriteMovie(), TMDBmovieDTO.class) : null;
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(TMDBmovieDTO::getId, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    // Necesitamos obtener el título de la película de nuevo.
                    // Esto es ineficiente, pero es para el ejemplo.
                    // Una mejor solución sería cachear los títulos.
                    String title = users.stream()
                            .map(user -> {
                                try {
                                    return user.getFavoriteMovie() != null ? objectMapper.readValue(user.getFavoriteMovie(), TMDBmovieDTO.class) : null;
                                } catch (JsonProcessingException e) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .filter(movie -> movie.getId() == entry.getKey())
                            .findFirst()
                            .map(TMDBmovieDTO::getTitle)
                            .orElse("Título no encontrado");
                    return new FavoriteMovieStat(entry.getKey(), title, entry.getValue());
                })
                .sorted((s1, s2) -> Long.compare(s2.getCount(), s1.getCount()))
                .limit(10)
                .collect(Collectors.toList());

        // 3. LOAD: Guardar el resumen transformado en MongoDB
        Document summaryDoc = new Document("_id", "global_summary")
                .append("totalUsers", totalUsers)
                .append("byGender", byGender)
                .append("byCountry", byCountry)
                .append("averageAge", averageAge)
                .append("topFavoriteMovies", topFavoriteMovies.stream()
                        .map(stat -> new Document("id", stat.getId())
                                .append("title", stat.getTitle())
                                .append("count", stat.getCount()))
                        .collect(Collectors.toList()));

        analyticsSummary().replaceOne(eq("_id", "global_summary"), summaryDoc, new ReplaceOptions().upsert(true));
    }
}
