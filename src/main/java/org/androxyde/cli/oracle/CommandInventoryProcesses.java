package org.androxyde.cli.oracle;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oratab;
import org.androxyde.oracle.Processes;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "processes")
public class CommandInventoryProcesses implements Callable<Integer> {

    @Inject
    ObjectMapper mapper;

    public void writeToStdout(Object o) {
        try {
            mapper.writeValue(System.out, o);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

    public Integer call() {

        LoggerUtils.setFile("/tmp/custom.log");

        Processes.load(true);

        writeToStdout(Processes.get());

        return 0;

    }

}
