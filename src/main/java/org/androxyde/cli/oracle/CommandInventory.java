package org.androxyde.cli.oracle;

import io.micronaut.context.annotation.Property;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.CentralInventory;
import org.androxyde.oracle.InventoryFactory;
import org.androxyde.utils.Json;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "inventory")
public class CommandInventory implements Callable<Integer> {

    @CommandLine.Option(names = {"--invPtr"}, description = "Pointer location")
    String invPtr;

    public Integer call() {
        LoggerUtils.setFile("custom.log");
        CentralInventory inv = InventoryFactory.get(invPtr);
        System.out.println(Json.prettyPrint(inv));
        return 0;
    }

}
