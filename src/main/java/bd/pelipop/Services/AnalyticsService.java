package bd.pelipop.Services;

import bd.pelipop.DTO.AnalyticsSummary;
import bd.pelipop.DTO.FavoriteMovieStat;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final MongoClient mongoClient;
    private final String dbName;

    public AnalyticsService(MongoClient mongoClient, @Value("${app.mongo.db}") String dbName) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
    }

    private MongoCollection<Document> analyticsSummary() {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db.getCollection("analytics_summary");
    }

    @SuppressWarnings("unchecked")
    public AnalyticsSummary buildSummary() {
        Document summaryDoc = analyticsSummary().find(new Document("_id", "global_summary")).first();

        if (summaryDoc == null) {
            // Ahora se puede usar el constructor con todos los argumentos.
            return new AnalyticsSummary(0L, Collections.emptyMap(), Collections.emptyMap(), 0.0, Collections.emptyList());
        }

        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setTotalUsers(summaryDoc.getLong("totalUsers"));

        Map<String, Long> byGender = (Map<String, Long>) summaryDoc.get("byGender");
        summary.setByGender(byGender.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)));

        Map<String, Long> byCountry = (Map<String, Long>) summaryDoc.get("byCountry");
        summary.setByCountry(byCountry.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new)));

        summary.setAverageAge(summaryDoc.getDouble("averageAge"));

        List<Document> topMoviesDocs = (List<Document>) summaryDoc.get("topFavoriteMovies");
        if (topMoviesDocs != null) {
            List<FavoriteMovieStat> topMovies = topMoviesDocs.stream()
                    .map(doc -> new FavoriteMovieStat(doc.getLong("id"), doc.getString("title"), doc.getLong("count")))
                    .collect(Collectors.toList());
            summary.setTopFavoriteMovies(topMovies);
        } else {
            summary.setTopFavoriteMovies(Collections.emptyList());
        }

        return summary;
    }
}
