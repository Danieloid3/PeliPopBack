package bd.pelipop.Payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TmdbCreateListRequest {

    @NotBlank
    private String sessionId;

    @NotBlank
    private String name;

    private String description;

    private String language;
}
