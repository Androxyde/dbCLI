package org.androxyde.oracle.process;

import org.androxyde.oracle.home.Homes;
import org.androxyde.utils.OS;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.StreamConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;

public class ProcessConsumer implements StreamConsumer {

    OracleProcesses processes;
    ExecutorService pool;

    public ProcessConsumer(OracleProcesses processes, ExecutorService pool) {
        this.processes = processes;
        this.pool = pool;
    }

    public void processDetails(OracleProcess p) {

        Runnable r = new Runnable() {
            @Override
            public void run() {
                ProcessEnvConsumer c = new ProcessEnvConsumer(p);
                if (p!=null) {
                    ProcBuilder pb = new ProcBuilder("ps")
                            .withArg("ewwo")
                            .withArg("command")
                            .withArg(p.getPid().toString())
                            .withOutputConsumer(c);
                    OS.executeRaw(pb);
                }
            }
        };
        pool.submit(r);
    }

    @Override
    public void consume(InputStream stream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;

        while ((line = reader.readLine()) != null) {

            String[] splitted=line.replaceAll("-[a-z_]*","").split("\\s+");

            if (line.matches(".*tnslsnr.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("LISTENER")
                        .name(splitted[splitted.length-1])
                        .pid(Long.parseLong(splitted[1]))
                        .homeLocation(Homes.sanitize(splitted[splitted.length-2].replaceAll("/bin/tnslsnr","")))
                        .build();
                processes.getListeners().add(p);
                processDetails(p);
            }
            else if (line.matches(".*ora_pmon.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("DATABASE")
                        .name(splitted[splitted.length-1].replaceAll("ora_pmon_",""))
                        .pid(Long.parseLong(splitted[1]))
                        .build();
                processes.getDatabases().add(p);
                processDetails(p);
            }
            else if (line.matches(".*emwd\\.pl.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("AGENT")
                        .pid(Long.parseLong(splitted[1]))
                        .homeLocation(Homes.sanitize(splitted[2].replaceAll("/perl/bin/perl","")))
                        .build();
                processes.getAgents().add(p);
            }
            else if (line.matches(".*EMGC_OMS.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("OMS")
                        .pid(Long.parseLong(splitted[1]))
                        .homeLocation(Homes.sanitize(splitted[2].replaceAll("/oracle_common/jdk/bin/java","")))
                        .build();
                processes.getOms().add(p);
            }
        }
    }
}