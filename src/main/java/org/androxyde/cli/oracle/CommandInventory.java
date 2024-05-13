package org.androxyde.cli.oracle;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.Oratab;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "oracle",
        subcommands = {
        CommandInventoryOratab.class,
        CommandInventoryCentralInventory.class,
        CommandInventoryProcesses.class
})
public class CommandInventory implements Callable<Integer> {

    public Integer call() {

        return 1;

    }

}
