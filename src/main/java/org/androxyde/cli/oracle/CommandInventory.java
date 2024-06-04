package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Slf4j
@Command(name = "oracle",
        subcommands = {
        CommandInventoryOratab.class,
        CommandInventoryCentralInventory.class,
        CommandInventoryProcesses.class,
        CommandInventoryHomes.class
})
public class CommandInventory implements Callable<Integer> {

    public Integer call() {

        return 1;

    }

}
