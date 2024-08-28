package org.androxyde.oracle.oratab;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.home.Homes;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class Oratab {

    private static Map<String,OratabEntry> entries = new HashMap<>();

    public static void load(Boolean force, String location) {

        if (force) entries.clear();

        try {
        for (String line : Files.readAllLines(Paths.get(location))) {
                if (line.matches("^[A-Za-z][A-Za-z0-9_]+:.*:[YyNn].*")) {
                    String[] splitted=line.split(":");
                    try {
                        OratabEntry entry = OratabEntry.builder()
                                .oracleSid(splitted[0])
                                .homeLocation(splitted[1])
                                .autoStart(splitted[2].substring(0, 1))
                                .comment(splitted[2].indexOf("#") > 0 ? splitted[2].substring(splitted[2].indexOf("#") + 1).trim() : null)
                                .build();
                        entries.put(entry.getOracleSid(), entry);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                } else if (line.matches("^#(\\s+)?[A-Za-z][A-Za-z0-9_]+:.*:[YyNn].*")) {
                    String[] splitted=line.split(":");
                    OratabEntry entry = OratabEntry.builder()
                            .oracleSid(splitted[0])
                            .homeLocation(splitted[1])
                            .autoStart(splitted[2].substring(0, 1))
                            .comment(splitted[2].indexOf("#") > 0 ? splitted[2].substring(splitted[2].indexOf("#") + 1).trim() : null)
                            .build();
                    entries.put(entry.getOracleSid(), entry);
            }
        }
        } catch (Exception ioe) {
            log.error(ioe.getMessage());
        }
    }

    public static Collection<OratabEntry> get() {
        return entries.values();
    }

}
