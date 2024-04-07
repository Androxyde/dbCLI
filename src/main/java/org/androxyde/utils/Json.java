package org.androxyde.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Json {

    public static String prettyPrint(Object o) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            // pretty print
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (Exception e) {
            return "{}";
        }

    }

}
