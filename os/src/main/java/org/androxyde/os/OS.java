package org.androxyde.os;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.*;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.ProcResult;
import org.buildobjects.process.StreamConsumer;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class OS {

    public static final AtomicLong SEQ_ID = new AtomicLong(1L);

    public static Map<String, UserInfos> users = new HashMap<>();

    public static void CommandExec(String command) {
        try {
            Long id = OS.SEQ_ID.getAndIncrement();
            log.info("OS_RUN:"+id+":BEGIN:"+command);
            CommandLine cl = CommandLine.parse(command);
            Executor exec = DefaultExecutor.builder().get();
            exec.setStreamHandler(new PumpStreamHandler(new StdoutStreamParser(id), new StdErrStreamParser(id)));
            exec.setProcessDestroyer(new ShutdownHookProcessDestroyer());
            int result = exec.execute(cl);
            log.info("OS_RUN:"+id+":END:"+result);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static CommandResult execute(ProcBuilder proc, Map env, StreamConsumer consumer) {

        Long id = OS.SEQ_ID.getAndIncrement();

        log.info("OS_RUN:"+id+":BEGIN:" + proc.getCommandLine());

        CommandResult result = CommandResult.builder().build();

        if (consumer==null) {
            consumer = new StreamConsumer() {
                @Override
                public void consume(InputStream stream) throws IOException {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.addStdout(line);
                    }

                }
            };
        }

        try {
            ProcResult r = proc.withOutputConsumer(consumer).run();
            result.setReturnCode(r.getExitValue());
            result.setStdOutComplete(true);
        } catch (Exception e) {
            result.setReturnCode(1);
            log.error("OS_RUN:"+id+":"+e.getMessage());
            result.setStdOutComplete(false);
        }
        log.info("OS_RUN:"+id+":END:"+result.getReturnCode());
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

    public static Properties toProperties(String filePath) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File(filePath)));
        } catch (Exception e) {
            log.error("Error converting "+filePath+" to Properties : "+e.getMessage());
        }
        return p;
    }

    public static UserInfos getUserInfos(String username) {

        if (users.containsKey(username)) return users.get(username);

        UserInfos infos = UserInfos.builder().build();

        ProcBuilder pb = new ProcBuilder("id")
                .withArg(username);

        OS.execute(pb,null,new IdConsumer(infos));

        users.put(username, infos);

        return infos;

    }

    public static String currentUser() {

        ProcBuilder pb = new ProcBuilder("id")
                .withArg("-un");

        CommandResult r = OS.execute(pb,null,null);

        try {
            return r.getStdout().get(0).trim();
        } catch (Exception e) {
            return "";
        }

    }
}