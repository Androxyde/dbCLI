package org.androxyde.cli.oracle;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oracle;
import org.androxyde.os.Json;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "centralinventory")
public class CommandInventoryCentralInventory implements Callable<Integer> {

    @Option(required=false, names = {"--invPtrLoc"}, description = "Inventory Pointer location")
    String invPtrLoc="/etc/oraInst.loc";
    @Option(required=false, names = {"--logFile"}, description = "Logfile location")
    String logFile="/tmp/oracle_centralinventory.log";

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

        Oracle oracle = Oracle.builder().withcentralinventory(true).withtools(false).build();

        oracle.computeHomes();

        Json.toStdout(oracle);

        return 0;

    }

}
