package org.androxyde.oracle.process;

import org.androxyde.utils.OS;
import org.buildobjects.process.ProcBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Processes {

    private final static OracleProcesses processes = OracleProcesses.builder().build();

    public static void load(Boolean force) {

        ExecutorService pool = Executors.newFixedThreadPool(3);

        ProcBuilder pb = new ProcBuilder("ps")
                .withArg("-eo")
                .withArg("user,pid,command")
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
