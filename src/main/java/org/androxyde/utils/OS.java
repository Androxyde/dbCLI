package org.androxyde.utils;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.process.ProcessConsumer;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StreamConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;

@Slf4j
public class OS {

    public static Map<String, UserInfos> users = new HashMap<>();

    public static ProcResult executeRaw(ProcBuilder proc) {
        log.info("Command line : " + proc.getCommandLine());
        return proc.run();
    }

    public static CommandResult execute(ProcBuilder proc) {
        CommandResult result = CommandResult.builder().build();
        ProcResult r = proc.withOutputConsumer(new StreamConsumer() {
            @Override
            public void consume(InputStream stream) throws IOException {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.addStdout(line);
                    }

            }
        }).run();

        result.setReturnCode(r.getExitValue());
        result.setStdOutComplete(true);

        return result;

    }

    public static OSName getAllNicAddresses() {

        try {
            InetAddress localComputer = InetAddress.getLocalHost();

            return OSName.builder()
                    .hostname(localComputer.getHostName())
                    .domainname(localComputer.getCanonicalHostName().replaceAll("^"+localComputer.getHostName(),""))
                    .build();
        } catch (UnknownHostException e) {}

        return OSName.builder().build();
    }

    public static UserInfos getUserInfos(String username) {

        if (users.containsKey(username)) return users.get(username);

        UserInfos infos = UserInfos.builder().build();

        ProcBuilder pb = new ProcBuilder("id")
                .withArg(username)
                .withOutputConsumer(new IdConsumer(infos));

        OS.executeRaw(pb);

        users.put(username, infos);

        return infos;

    }

}