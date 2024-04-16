package org.androxyde.cli.oracle;

import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.CentralInventory;
import org.androxyde.oracle.InventoryFactory;
import org.androxyde.utils.Json;
import picocli.CommandLine;
import java.util.concurrent.Callable;

@Introspected
@Singleton
@Slf4j
@CommandLine.Command(name = "inventory")
public class CommandInventory implements Callable<Integer> {

    @CommandLine.Option(required=true, names = {"--invPtr"}, description = "Pointer location")
    String invPtr;

    public Integer call() {
        LoggerUtils.setFile("custom.log");
        try {
            CentralInventory inv = InventoryFactory.get(invPtr);
            System.out.println(Json.prettyPrint(inv));
        }
        catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        return 0;

    }

}
