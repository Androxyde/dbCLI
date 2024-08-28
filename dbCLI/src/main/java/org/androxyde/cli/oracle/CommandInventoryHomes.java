package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oracle;
import org.androxyde.os.Json;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "homes")
public class CommandInventoryHomes implements Callable<Integer> {

    @Option(required=false, names = {"--invPtrLoc"}, description = "Pointer location")
    String invPtrLoc="/etc/oraInst.loc";
    @Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/oracle_homes.log";
    @Option(required=false, names = {"--computehomes"}, description = "Compute oracle homes")
    Boolean computeoh=Boolean.FALSE;
    @Option(required=false, names = {"--computedatabases"}, description = "Compute databases")
    Boolean computedb=Boolean.FALSE;

    public Integer call() {

        LoggerUtils.setFile(logFile);

        Oracle oracle = Oracle.builder().withoratab(true).withprocesses(true).withcentralinventory(true).withgchomes(true).build();

        if (computeoh)
            oracle.computeHomes();

        if (computedb)
            oracle.computeDatabases();

        Json.toStdout(oracle);

        return 0;

    }

}
