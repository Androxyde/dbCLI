package org.androxyde.oracle.process;

import org.androxyde.os.OS;
import org.buildobjects.process.ProcBuilder;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Processes {

    private final static OracleProcesses processes = OracleProcesses.builder().build();

    public static void load(Boolean force) {

        ExecutorService pool = Executors.newFixedThreadPool(3);

        ProcBuilder pb = new ProcBuilder("ps")
                .withArg("-eo")
                .withArg("user,pid,args")
                .withOutputConsumer(new ProcessConsumer(processes, pool));

        OS.executeRaw(pb);

        pool.shutdown();

        while (!pool.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {}
        }

    }

    public static OracleProcesses get() {
        return processes;
    }

}
