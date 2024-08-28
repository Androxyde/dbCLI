package org.androxyde.cli.os;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.os.OS;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "run")
public class CommandOSRun implements Callable<Integer> {

    @Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/os_run.log";

    @Option(required=true, names = {"--command"}, description = "Inventory Pointer location")
    String command;

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

        LoggerUtils.setFile(logFile);

        OS.CommandExec(command);

        return 0;

    }

}
