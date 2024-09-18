package org.androxyde.oracle;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.oratab.OratabEntry;
import org.androxyde.oracle.process.OracleProcesses;
import org.androxyde.oracle.process.ProcessConsumer;
import org.buildobjects.process.ProcBuilder;
import org.androxyde.os.OS;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class OraUtils {

    public static Set<OratabEntry> getOratab(String location) {

        Set<OratabEntry> entries = new HashSet<>();

        try {
            for (String line : Files.readAllLines(Paths.get(location))) {
                if (line.matches("^[A-Za-z][A-Za-z0-9_]+:.*:[YyNn].*")) {
                    String[] splitted=line.split(":");
                    try {
                        OratabEntry entry = OratabEntry.builder()
                                .oracleSid(splitted[0])
                                .homeLocation(splitted[1])
                                .autoStart(splitted[2].substring(0, 1))
                                .comment(splitted[2].indexOf("#") > 0 ? splitted[2].substring(splitted[2].indexOf("#") + 1).trim() : null)
                                .build();
                        entries.add(entry);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                } else if (line.matches("^#(\\s+)?[A-Za-z][A-Za-z0-9_]+:.*:[YyNn].*")) {
                    String[] splitted=line.split(":");
                    OratabEntry entry = OratabEntry.builder()
                            .oracleSid(splitted[0])
                            .homeLocation(splitted[1])
                            .autoStart(splitted[2].substring(0, 1))
                            .comment(splitted[2].indexOf("#") > 0 ? splitted[2].substring(splitted[2].indexOf("#") + 1).trim() : null)
                            .build();
                    entries.add(entry);
                }
            }
        } catch (Exception ioe) {
            log.error(ioe.getMessage());
        }

        return entries;

    }

    public static OracleProcesses getOracleProcesses(OracleProcesses reference) {

        OracleProcesses processes = OracleProcesses.builder().build();

        try {
            ExecutorService pool = Executors.newFixedThreadPool(3);

            ProcBuilder pb = new ProcBuilder("ps")
                    .withArg("-eo")
                    .withArg("user,pid,args");

            OS.execute(pb,null,new ProcessConsumer(processes, reference, pool));

            pool.shutdown();

            while (!pool.isTerminated()) {
                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return processes;

    }

    public static String sanitize(String path) {
        while (path.endsWith("/")) {
            return sanitize(path.substring(0, path.length() - 1));
        }
        return path.replaceAll("/+","/");
    }

}
