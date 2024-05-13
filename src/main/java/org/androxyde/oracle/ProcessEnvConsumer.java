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

public class ProcessEnvConsumer implements StreamConsumer {

    OracleProcess process;

    public ProcessEnvConsumer(OracleProcess p) {
        process=p;
    }

    public OracleProcess getProcess() {
        return process;
    }

    @Override
    public void consume(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] splitted = line.split("\\s+");
            for (String elem : splitted) {
                String[] elems = elem.split("=");
                if (elems.length == 2) {
                    System.out.println(elem);
                    if (elems[0].equals("ORACLE_HOME")) {
                        process.setHomeLocation(elems[1]);
                    }
                    if (elems[0].equals("TNS_ADMIN")) {
                        process.setTnsAdminLocation(elems[1]);
                    }
                }
            }
        }
        System.out.println(process);
    }

}