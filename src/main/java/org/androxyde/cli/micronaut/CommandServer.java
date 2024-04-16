package org.androxyde.cli.oracle;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.runtime.Micronaut;
import org.androxyde.logger.LoggerUtils;
import picocli.CommandLine;
import java.util.concurrent.Callable;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
        info = @Info(
                title = "Hello World",
                version = "0.0",
                description = "My API",
                license = @License(name = "Apache 2.0", url = "https://foo.bar"),
                contact = @Contact(url = "https://gigantic-server.com", name = "Fred", email = "Fred@gigagantic-server.com")
        )
)
@CommandLine.Command(name = "server")
@Introspected
public class CommandServer implements Callable<Integer> {

    public Integer call() {
        LoggerUtils.setFile("custom.log");
        Micronaut.run(CommandServer.class);
        return 0;

    }

}