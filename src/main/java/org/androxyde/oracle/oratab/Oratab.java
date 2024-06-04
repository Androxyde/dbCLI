package org.androxyde.oracle.oratab;

import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.home.Homes;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@Slf4j
public class Oratab {

    private static Map<String,OratabEntry> entries = new HashMap<>();

    public static void load(Boolean force, String location) {

        if (force) entries.clear();

        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(location)))) {
            while (sc.hasNextLine()) {
                String line=sc.nextLine();
                if (line.matches("^[A-Za-z][A-Za-z0-9]+:.*")) {
                    String[] parts = line.split(":");
                    if (parts.length>=3) {
                        OratabEntry e = new OratabEntry(parts[0],
                                parts[1],
                                parts[2].split("#")[0].trim(),
                                null);
                        e.setComment(StringUtils.removeStart(line,e.getOracleSid()+":"+
                                                                         e.getHomeLocation()+":"+
                                                                         e.getAutoStart()).trim().replaceAll("^[#]+","").trim());
                        entries.put(e.getOracleSid(),e);
                        Homes.add(e.getHomeLocation());
                    }
                }
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
    }

    public static Collection<OratabEntry> get() {
        return entries.values();
    }

}
