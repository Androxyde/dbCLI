package org.androxyde.cli.oracle;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.CentralInventories;
import org.androxyde.oracle.Oratab;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "centralinventory")
public class CommandInventoryCentralInventory implements Callable<Integer> {

    @Option(required=false, names = {"--invPtrLoc"}, description = "Pointer location")
    String invPtrLoc="/etc/oraInst.loc";

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

        CentralInventories.add(invPtrLoc);

        writeToStdout(CentralInventories.get());

        return 0;

    }

}
