package bd.pelipop.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummary {
    private long totalUsers;
    private Map<String, Long> byGender;
    private Map<String, Long> byCountry;
    private double averageAge;
    private List<FavoriteMovieStat> topFavoriteMovies;
}
