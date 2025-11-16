package bd.pelipop.DTO;

import bd.pelipop.DTO.FavoriteMovieStat;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class AnalyticsSummary {
    private long totalUsers;
    private Map<String, Long> byGender;
    private Map<String, Long> byCountry;
    private double averageAge;
    private List<FavoriteMovieStat> topFavoriteMovies;
}
