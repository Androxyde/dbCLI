package org.androxyde.oracle;

import org.androxyde.utils.CommandResult;
import org.androxyde.utils.OS;
import org.buildobjects.process.ProcBuilder;
import org.buildobjects.process.StreamConsumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

public class ProcessConsumer implements StreamConsumer {

    Set<OracleProcess> processes;

    public ProcessConsumer(Set<OracleProcess> p) {
        processes=p;
    }

    public OracleProcess getDetails(OracleProcess p) {
        ProcessEnvConsumer c = new ProcessEnvConsumer(p);
        if (p!=null) {
            ProcBuilder pb = new ProcBuilder("ps")
                    .withArg("ewwo")
                    .withArg("command")
                    .withArg(p.getPid().toString())
                    .withOutputConsumer(c);
            OS.executeRaw(pb);
        }
        return c.getProcess();
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
                        .homeLocation(splitted[splitted.length-2].replaceAll("/bin/tnslsnr",""))
                        .build();
                processes.add(getDetails(p));
            }
            else if (line.matches(".*ora_pmon.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("DATABASE")
                        .name(splitted[splitted.length-1].replaceAll("ora_pmon_",""))
                        .pid(Long.parseLong(splitted[1]))
                        .build();
                processes.add(getDetails(p));
            }
            else if (line.matches(".*emwd\\.pl.*")) {
                OracleProcess p = OracleProcess.builder()
                        .owner(splitted[0])
                        .type("AGENT")
                        .pid(Long.parseLong(splitted[1]))
                        .homeLocation(splitted[2].replaceAll("/perl/bin/perl",""))
                        .build();
                processes.add(getDetails(p));
            }

        }
    }
}