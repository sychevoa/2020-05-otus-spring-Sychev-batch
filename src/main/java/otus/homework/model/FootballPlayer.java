package otus.homework.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document(collection = "players")
public class FootballPlayer {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private Team team;
    private Country country;

    public FootballPlayer(String firstName, String lastName, Team team, Country country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.team = team;
        this.country = country;
    }
}
