package bd.pelipop.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TMDBmovieResponse {

    private int page;

    @JsonProperty("results")
    private List<TMDBmovieDTO> results;

    @JsonProperty("total_pages")
    private int totalPages;

    @JsonProperty("total_results")
    private int totalResults;
}
