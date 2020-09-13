package otus.homework.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class BatchCommands {

    private final JobOperator jobOperator;

    public final static String JOB_NAME = "importFootballPlayerJob";

    @ShellMethod(value = "startMigrationJ", key = "start")
    public void startMigrationJobWithJobOperator() throws Exception {
        jobOperator.start(JOB_NAME, "");
    }
}