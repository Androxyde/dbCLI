package org.androxyde.cli;

import io.micronaut.context.annotation.Property;
import io.micronaut.core.annotation.Introspected;
import jakarta.inject.Singleton;
import org.androxyde.cli.oracle.CommandInventory;
import org.androxyde.cli.oracle.CommandServer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.util.concurrent.Callable;

@Introspected
@Singleton
@Command(name = "dbCLI", description = "...", mixinStandardHelpOptions = true,
        subcommands = { CommandInventory.class, CommandServer.class }
)
public class dbCLI implements Callable<Integer> {

    @Option(names = {"-v", "--verbose"}, description = "...")
    boolean verbose;

    @Option(names = {"--env"}, description = "...")
    String env;

    @Property(name = "api.url")
    String propertyName;

    public Integer call() {
        // business logic here
        if (verbose) {
            System.out.println(propertyName);
        }
        return 0;
    }

}
