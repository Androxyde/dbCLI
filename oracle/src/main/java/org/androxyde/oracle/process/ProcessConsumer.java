package org.androxyde.oracle.process;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.OraUtils;
import org.androxyde.oracle.home.Homes;
import org.androxyde.os.OS;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.StreamConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

@Slf4j
public class ProcessConsumer implements StreamConsumer {

    OracleProcesses processes;
    OracleProcesses references;
    ExecutorService pool;

    public ProcessConsumer(OracleProcesses processes, OracleProcesses references, ExecutorService pool) {
        this.processes = processes;
        this.references=Optional.ofNullable(references).orElse(OracleProcesses.builder().build());
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
                            .withArg(p.getPid().toString());
                    OS.execute(pb,null,c);
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

            try {

                Long pid=null;

                if (!splitted[1].equals("PID"))
                    pid=Long.parseLong(splitted[1]);

                if (!line.matches(".*grep.*")) {
                    if (line.matches(".*tnslsnr.*")) {
                        if (references.getIndex().containsKey(pid)) {
                            processes.add("listener", references.getIndex().get(pid));
                        } else {
                            OracleProcess p = OracleProcess.builder()
                                    .owner(splitted[0])
                                    .type("LISTENER")
                                    .name(splitted[splitted.length - 1])
                                    .pid(pid)
                                    .homeLocation(OraUtils.sanitize(splitted[splitted.length - 2].replaceAll("/bin/tnslsnr", "")))
                                    .build();
                            processes.add("listener", p);
                            processDetails(p);
                        }
                    } else if (line.matches(".*ora_pmon.*")) {
                        if (references.getIndex().containsKey(pid)) {
                            processes.add("database", references.getIndex().get(pid));
                        } else {
                            OracleProcess p = OracleProcess.builder()
                                    .owner(splitted[0])
                                    .type("DATABASE")
                                    .name(splitted[splitted.length - 1].replaceAll("ora_pmon_", ""))
                                    .pid(pid)
                                    .build();
                            processes.add("database", p);
                            processDetails(p);
                        }
                    } else if (line.matches(".*emwd\\.pl.*")) {
                        if (references.getIndex().containsKey(pid)) {
                            processes.add("agent", references.getIndex().get(pid));
                        } else {
                            OracleProcess p = OracleProcess.builder()
                                    .owner(splitted[0])
                                    .type("AGENT")
                                    .pid(Long.parseLong(splitted[1]))
                                    .homeLocation(OraUtils.sanitize(splitted[2].replaceAll("/perl/bin/perl", "")))
                                    .build();
                            processes.add("agent", p);
                            processDetails(p);
                        }
                    } else if (line.matches(".*EMGC_OMS.*")) {
                        if (references.getIndex().containsKey(pid)) {
                            processes.add("oms", references.getIndex().get(pid));
                        } else {
                            OracleProcess p = OracleProcess.builder()
                                    .owner(splitted[0])
                                    .type("OMS")
                                    .pid(pid)
                                    .homeLocation(OraUtils.sanitize(splitted[2].replaceAll("/oracle_common/jdk/bin/java", "")))
                                    .build();
                            processes.add("oms", p);
                            processDetails(p);
                        }
                    } else if (line.matches(".*ohasd\\.bin.*")) {
                        if (references.getIndex().containsKey(pid)) {
                            processes.add("crs", references.getIndex().get(pid));
                        } else {
                            OracleProcess p = OracleProcess.builder()
                                    .owner(splitted[0])
                                    .type("CRS")
                                    .pid(pid)
                                    .homeLocation(OraUtils.sanitize(splitted[2].replaceAll("/bin/ohasd.bin", "")))
                                    .build();
                            processes.add("crs", p);
                            processDetails(p);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
}