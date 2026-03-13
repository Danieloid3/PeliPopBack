package bd.pelipop.Payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TmdbAddMovieToListRequest {

    @NotBlank
    private String sessionId;

    @NotNull
    private Long mediaId;
}
