package org.androxyde.oracle.process;

import lombok.Getter;
import org.androxyde.oracle.OraUtils;
import org.androxyde.oracle.home.Homes;
import org.buildobjects.process.StreamConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Getter
public class ProcessEnvConsumer implements StreamConsumer {

    OracleProcess process;

    public ProcessEnvConsumer(OracleProcess p) {
        process=p;
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
                    if (elems[0].equals("ORACLE_HOME") && process.getHomeLocation()==null) {
                        process.setHomeLocation(OraUtils.sanitize(elems[1]));
                    }
                    if (elems[0].equals("TNS_ADMIN")) {
                        process.setTnsAdminLocation(OraUtils.sanitize(elems[1]));
                    }
                }
            }
        }
        if (process.getTnsAdminLocation()==null) process.setTnsAdminLocation(process.getHomeLocation()+"/network/admin");
    }

}