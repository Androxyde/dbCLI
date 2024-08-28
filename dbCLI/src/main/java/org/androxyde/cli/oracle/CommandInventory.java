package org.androxyde.cli.oracle;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "oracle",
        subcommands = {
        CommandInventoryOratab.class,
        CommandInventoryCentralInventory.class,
        CommandInventoryProcesses.class,
        CommandInventoryGCHomes.class,
        CommandInventoryHomes.class
})
public class CommandInventory implements Callable<Integer> {

    @Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
    private boolean helpRequested = false;

    public Integer call() {

        return 1;

    }

}
