package bd.pelipop.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "genre")
public class Genre {
    @Id
    @Column(name = "genre_id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

}