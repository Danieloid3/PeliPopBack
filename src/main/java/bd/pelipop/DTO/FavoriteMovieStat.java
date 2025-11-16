// java
package bd.pelipop.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteMovieStat {
    private long id;
    private String title;
    private long count;
}
