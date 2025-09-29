package bd.pelipop.Payload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;

    public AuthResponse(String token, Long userId, String email, String username) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.username = username;
    }



}

