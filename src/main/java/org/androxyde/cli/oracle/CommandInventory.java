package org.androxyde.cli.oracle;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.serde.ObjectMapper;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.CentralInventories;
import org.androxyde.oracle.CentralInventory;
import org.androxyde.oracle.Oratab;
import org.androxyde.utils.OS;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@CommandLine.Command(name = "inventory")
public class CommandInventory implements Callable<Integer> {

    @CommandLine.Option(required=false, names = {"--invPtr"}, description = "Pointer location")
    String invPtr="/etc/oraInst.loc";

    @Inject
    ObjectMapper mapper;

    public Integer call() {

        LoggerUtils.setFile("/tmp/custom.log");

        CentralInventories.add("/etc/oraInst.loc");

        Oratab.load(true);

        try {
            mapper.writeValue(System.out, Oratab.get());
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }

        return 0;

    }

}
