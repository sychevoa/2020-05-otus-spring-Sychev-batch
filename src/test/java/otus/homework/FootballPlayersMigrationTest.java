package otus.homework;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static otus.homework.shell.BatchCommands.JOB_NAME;

@SuppressWarnings("all")
@DisplayName("Миграция футболистов из MongoDB в H2 embedded ")
@SpringBatchTest
@SpringBootTest
class FootballPlayersMigrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;
    @Autowired
    private JdbcTemplate template;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @DisplayName("выполнена успешно")
    @Test
    void shouldMigrateFootballPlayers() throws Exception {
        Job job = jobLauncherTestUtils.getJob();
        assertThat(job).isNotNull()
                .extracting(Job::getName)
                .isEqualTo(JOB_NAME);

        int sqlDBsizeBeforeMigration = template.queryForObject(
                "SELECT COUNT(*) FROM PLAYER", Integer.class);
        assertThat(sqlDBsizeBeforeMigration).isEqualTo(0);

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters());
        int sqlDBsizeAfterMigration = template.queryForObject(
                "SELECT COUNT(*) FROM PLAYER", Integer.class);
        assertThat(sqlDBsizeAfterMigration).isEqualTo(3);

        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }
}