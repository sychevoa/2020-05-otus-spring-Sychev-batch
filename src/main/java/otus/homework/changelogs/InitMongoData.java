package otus.homework.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.client.MongoDatabase;
import otus.homework.model.Country;
import otus.homework.model.FootballPlayer;
import otus.homework.model.Team;

import java.util.List;

@ChangeLog(order = "001")
public class InitMongoData {

    @ChangeSet(order = "000", author = "sychevoa", runAlways = true, id = "dropDB")
    public void dropDB(MongoDatabase database) {
        database.drop();
    }

    @ChangeSet(order = "001", author = "sychevoa", runAlways = true, id = "initData")
    public void init(MongockTemplate template) {
        List<Team> teams = createAndGetTeams();
        teams.forEach(template::save);

        List<Country> countries = createAndGetCountry();
        countries.forEach(template::save);

        template.save(new FootballPlayer("Cristiano", "Ronaldo", teams.get(0), countries.get(0)));
        template.save(new FootballPlayer("Kylian", "Mbappe", teams.get(1), countries.get(1)));
        template.save(new FootballPlayer("Artem", "Dzuba", teams.get(2), countries.get(2)));
        template.save(new FootballPlayer("Sergio", "Ramos", teams.get(3), countries.get(3)));
        template.save(new FootballPlayer("Joe", "Willock", teams.get(4), countries.get(4)));
        template.save(new FootballPlayer("Aleksandr", "Erokhin", teams.get(2), countries.get(2)));
        template.save(new FootballPlayer("Antoine ", "Griezmann", teams.get(5), countries.get(1)));
    }

    private List<Team> createAndGetTeams() {
        return List.of(new Team("Juventus"), new Team("Paris Saint-Germain"), new Team("Zenit"),
                new Team("Real Madrid"), new Team("Arsenal"), new Team("Barselona"));
    }

    private List<Country> createAndGetCountry() {
        return List.of(new Country("Portugal"), new Country("France"), new Country("Russia"),
                new Country("Spain"), new Country("England"));
    }
}
