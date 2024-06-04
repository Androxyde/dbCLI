package org.androxyde.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStreamWriter;

@Slf4j
public class Json {

    public static String prettyPrint(Object o) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            // pretty print
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "{}";
        }

    }

    public static void toStdout(Object o) {
        try {
            OutputStreamWriter out = new OutputStreamWriter(System.out);
            ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
            SequenceWriter sequenceWriter = objectWriter.writeValues(out);
            sequenceWriter.write(o);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            System.out.println("{}");
        }
    }

}
