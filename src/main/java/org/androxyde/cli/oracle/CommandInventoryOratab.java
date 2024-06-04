package org.androxyde.cli.oracle;

import io.micronaut.serde.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.logger.LoggerUtils;
import org.androxyde.oracle.oratab.Oratab;
import org.androxyde.utils.Json;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import java.io.IOException;
import java.util.concurrent.Callable;
import jakarta.inject.Inject;

@Slf4j
@Command(name = "oratab")
public class CommandInventoryOratab implements Callable<Integer> {

    @Option(required=false, names = {"--oratabLocation"}, description = "oratab Pointer location")
    String oratabLocation="/etc/oratab";

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

        Oratab.load(true, oratabLocation);

        System.out.println(Json.prettyPrint(Oratab.get()));
        //writeToStdout(Oratab.get());

        return 0;

    }

}
