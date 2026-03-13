package bd.pelipop.Payload;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TmdbRateMovieRequest {

    @NotBlank
    private String sessionId;

    @NotNull
    private Long movieId;

    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("10.0")
    private Double value;
}
