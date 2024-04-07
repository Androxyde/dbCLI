package org.androxyde.cli;

import io.micronaut.configuration.picocli.MicronautFactory;
import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import io.micronaut.context.annotation.Property;
import io.micronaut.context.env.Environment;
import jakarta.inject.Inject;
import org.androxyde.cli.oracle.CommandInventory;
import org.androxyde.logger.LoggerUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "dbCLI", description = "...", mixinStandardHelpOptions = true,
        subcommands = { CommandInventory.class }
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
