// java
package bd.pelipop.Services;

import bd.pelipop.DTO.AnalyticsSummary;
import bd.pelipop.DTO.FavoriteMovieStat;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final MongoClient mongoClient;
    private final String dbName;

    public AnalyticsService(MongoClient mongoClient, @Value("${app.mongo.db}") String dbName) {
        this.mongoClient = mongoClient;
        this.dbName = dbName;
    }

    private MongoCollection<Document> usersEtl() {
        MongoDatabase db = mongoClient.getDatabase(dbName);
        return db.getCollection("users_etl");
    }

    public AnalyticsSummary buildSummary() {
        MongoCollection<Document> col = usersEtl();

        long total = col.countDocuments();

        Map<String, Long> byGender = new HashMap<>();
        for (Document d : col.aggregate(List.of(
                Aggregates.group("$gender", Accumulators.sum("count", 1))
        ))) {
            String k = Objects.toString(d.get("_id"), "NULL");
            Number c = (Number) d.get("count");
            byGender.put(k, c == null ? 0L : c.longValue());
        }

        Map<String, Long> byCountry = new HashMap<>();
        for (Document d : col.aggregate(List.of(
                Aggregates.group("$country", Accumulators.sum("count", 1))
        ))) {
            String k = Objects.toString(d.get("_id"), "NULL");
            Number c = (Number) d.get("count");
            byCountry.put(k, c == null ? 0L : c.longValue());
        }

        List<FavoriteMovieStat> topFavs = new ArrayList<>();
        for (Document d : col.aggregate(List.of(
                Aggregates.match(new Document("favoriteMovie.id", new Document("$ne", null))),
                Aggregates.group(
                        new Document("id", "$favoriteMovie.id").append("title", "$favoriteMovie.title"),
                        Accumulators.sum("count", 1)
                ),
                Aggregates.sort(new Document("count", -1)),
                Aggregates.limit(10)
        ))) {
            Document id = (Document) d.get("_id");
            Number idNum = id != null ? (Number) id.get("id") : null;
            String title = id != null ? id.getString("title") : null;
            Number c = (Number) d.get("count");
            topFavs.add(new FavoriteMovieStat(
                    idNum == null ? 0L : idNum.longValue(),
                    title,
                    c == null ? 0L : c.longValue()
            ));
        }

        double avgAge = 0.0;
        for (Document d : col.aggregate(List.of(
                Aggregates.match(new Document("birthdate", new Document("$type", "date"))),
                Aggregates.group(
                        null,
                        new BsonField(
                                "avgAge",
                                new Document("$avg",
                                        new Document("$dateDiff",
                                                new Document("startDate", "$birthdate")
                                                        .append("endDate", "$$NOW")
                                                        .append("unit", "year")
                                        )
                                )
                        )
                )
        ))) {
            Number a = (Number) d.get("avgAge");
            avgAge = a == null ? 0.0 : a.doubleValue();
        }

        AnalyticsSummary summary = new AnalyticsSummary();
        summary.setTotalUsers(total);
        summary.setByGender(byGender.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new
                )));
        summary.setByCountry(byCountry.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new
                )));
        summary.setAverageAge(avgAge);
        summary.setTopFavoriteMovies(topFavs);
        return summary;
    }
}
