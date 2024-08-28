package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oracle;
import org.androxyde.os.Json;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "gchomes")
public class CommandInventoryGCHomes implements Callable<Integer> {

    @Option(required=false, names = {"--oratabLocation"}, description = "oratab Pointer location")
    String oratabLocation="/etc/oratab";
    @Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/oracle_gchomes.log";
    @Option(required=false, names = {"--computehomes"}, description = "Compute oracle homes")
    Boolean compute=Boolean.FALSE;


    public Integer call() {

        LoggerUtils.setFile(logFile);

        Oracle oracle = Oracle.builder().withgchomes(true).build();

        if (compute)
            oracle.computeHomes();

        Json.toStdout(oracle);

        return 0;

    }

}
