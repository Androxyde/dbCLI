package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oracle;
import org.androxyde.os.Json;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "processes")
public class CommandInventoryProcesses implements Callable<Integer> {

    @CommandLine.Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/oracle_processes.log";

    public Integer call() {

        LoggerUtils.setFile(logFile);

        Oracle oracle = Oracle.builder().withoratab(false).withprocesses(true).build();

        oracle.computeHomes();

        Json.toStdout(oracle);

        return 0;

    }

}
