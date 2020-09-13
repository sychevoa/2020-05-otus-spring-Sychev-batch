package otus.homework.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.builder.MongoItemReaderBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import otus.homework.model.FootballPlayer;

import javax.sql.DataSource;
import java.util.HashMap;

import static otus.homework.shell.BatchCommands.JOB_NAME;

@Slf4j
@SuppressWarnings("all")
@RequiredArgsConstructor
@Configuration
public class JobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final String MONGO_READER = "mongoReader";
    private static final String PLAYER_STEP = "playerStep";
    private static final String COUNTRY_STEP = "countryStep";
    private static final String TEAM_STEP = "teamStep";
    private static final int CHUNK_SIZE = 5;

    @StepScope
    @Bean
    public MongoItemReader<FootballPlayer> reader(MongoTemplate mongoTemplate) {

        return new MongoItemReaderBuilder<FootballPlayer>()
                .name(MONGO_READER)
                .template(mongoTemplate)
                .jsonQuery("{}")
                .targetType(FootballPlayer.class)
                .sorts(new HashMap<>())
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<FootballPlayer> playerWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FootballPlayer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO PLAYER (first_name, last_name, country_id, team_id) " +
                        "SELECT :firstName, :lastName, c.id, t.id " +
                        "FROM COUNTRY c " +
                        "JOIN TEAM t on t.nosql_id = :team.id " +
                        "WHERE c.nosql_id=:country.id")
                .dataSource(dataSource)
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<FootballPlayer> countryWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FootballPlayer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("MERGE INTO COUNTRY (name, nosql_id) KEY (nosql_id) VALUES (:country.name, :country.id)")
                .dataSource(dataSource)
                .build();
    }

    @StepScope
    @Bean
    public JdbcBatchItemWriter<FootballPlayer> teamWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<FootballPlayer>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("MERGE INTO TEAM (title, nosql_id) KEY (nosql_id) VALUES (:team.title, :team.id)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step playerStep(JdbcBatchItemWriter playerWriter, ItemReader reader) {
        return stepBuilderFactory.get(PLAYER_STEP)
                .chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(playerWriter)
                .build();
    }

    @Bean
    public Step countryStep(JdbcBatchItemWriter countryWriter, ItemReader reader) {
        return stepBuilderFactory.get(COUNTRY_STEP)
                .chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(countryWriter)
                .build();
    }

    @Bean
    public Step teamStep(JdbcBatchItemWriter teamWriter, ItemReader reader) {
        return stepBuilderFactory.get(TEAM_STEP)
                .chunk(CHUNK_SIZE)
                .reader(reader)
                .writer(teamWriter)
                .build();
    }

    @Bean
    public Job importFootballPlayerJob(Step countryStep, Step teamStep, Step playerStep) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(countryStep)
                .next(teamStep)
                .next(playerStep)
                .build();
    }
}
