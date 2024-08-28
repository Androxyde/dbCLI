package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oracle;
import org.androxyde.os.Json;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "oratab")
public class CommandInventoryOratab implements Callable<Integer> {

    @Option(required=false, names = {"--oratabLocation"}, description = "oratab Pointer location")
    String oratabLocation="/etc/oratab";
    @Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/oracle_oratab.log";


    public Integer call() {

        LoggerUtils.setFile(logFile);

        Oracle oracle = Oracle.builder().withoratab(true).withprocesses(false).build();

        oracle.computeHomes();

        Json.toStdout(oracle);

        return 0;

    }

}
