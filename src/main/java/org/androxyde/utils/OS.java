package org.androxyde.utils;

import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@Slf4j
public class OS {

    public static CommandResult execute(ProcBuilder proc) {
        CommandResult result = CommandResult.builder().build();
        ProcResult r = proc.withOutputConsumer(new StreamConsumer() {
            @Override
            public void consume(InputStream stream) throws IOException {
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while(reader.ready()) {
                    result.addStdout(reader.readLine());
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

}