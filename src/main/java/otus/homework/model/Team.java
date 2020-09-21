package otus.homework.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "teams")
public class Team {
    @Id
    private String id;
    private String title;

    public Team(String title) {
        this.title = title;
    }
}
