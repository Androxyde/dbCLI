package org.androxyde.oracle;

import org.androxyde.utils.OS;
import org.buildobjects.process.ProcBuilder;
import java.util.HashSet;
import java.util.Set;

public class Processes {

    private static Set<OracleProcess> processes = new HashSet<>();


    public static void load(Boolean force) {

        ProcBuilder pb = new ProcBuilder("ps")
                .withArg("-eo")
                .withArg("user,pid,command")
                .withOutputConsumer(new ProcessConsumer(processes));

        OS.executeRaw(pb);

    }

    public static Set<OracleProcess> get() {
        return processes;
    }

}
