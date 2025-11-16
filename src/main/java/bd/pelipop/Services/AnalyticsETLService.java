// java
package bd.pelipop.Services;

import bd.pelipop.DTO.TMDBmovieDTO;
import bd.pelipop.Models.User;
import bd.pelipop.Repositories.UserRepository;
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
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

    private MongoCollection<Document> usersEtl() {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db.getCollection("users_etl");
    }

    private static Date toDate(LocalDate ld) {
        if (ld == null) return null;
        return Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private Document favoriteMovieDoc(String favoriteMovieJson) {
        if (favoriteMovieJson == null) return null;
        try {
            TMDBmovieDTO dto = objectMapper.readValue(favoriteMovieJson, TMDBmovieDTO.class);
            return new Document("id", dto.getId()).append("title", dto.getTitle());
        } catch (Exception e) {
            return null;
        }
    }

    private Document toUserDoc(User u) {
        return new Document("_id", u.getId())
                .append("email", u.getEmail())
                .append("username", u.getUsername())
                .append("role", u.getRole())
                .append("gender", u.getGender())
                .append("country", u.getCountry())
                .append("birthdate", toDate(u.getBirthdate()))
                .append("favoriteMovie", favoriteMovieDoc(u.getFavoriteMovie()));
    }

    public void upsertUser(User user) {
        if (user == null || user.getId() == null) return;
        usersEtl().replaceOne(eq("_id", user.getId()), toUserDoc(user), new ReplaceOptions().upsert(true));
    }

    public void removeUser(long userId) {
        usersEtl().deleteOne(eq("_id", userId));
    }

    @Scheduled(cron = "${app.analytics.cron}")
    public void syncUsersToMongo() {
        List<User> all = userRepository.findAll();
        MongoCollection<Document> col = usersEtl();
        for (User u : all) {
            col.replaceOne(eq("_id", u.getId()), toUserDoc(u), new ReplaceOptions().upsert(true));
        }
    }
}
