package org.androxyde.oracle.home;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class Homes {

    private static Set<String> processed = new HashSet<>();
    private static final OracleHomes oracleHomes = OracleHomes.builder().build();

    public static void add(String path) {
        String sanitized_path=Homes.sanitize(path);
        if (processed.add(sanitized_path)) {
            Home h = Home.builder().homeLocation(sanitized_path).build();
            h.fetchLocalInventory();
            if ("RDBMS".equals(h.getHomeType())) {
                RDBMSHome lh = RDBMSHome.builder().homeLocation(h.getHomeLocation()).homeRelease(h.getHomeRelease()).build();
                        lh.fetchOwner();
                oracleHomes.rdbmsHomes.add(lh);
            }
            else if ("AGENT".equals(h.getHomeType())) {
                oracleHomes.agentHomes.add(AGENTHome.builder().homeLocation(h.getHomeLocation()).homeRelease(h.getHomeRelease()).build());
            }
        }
    }

    public static String sanitize(String path) {
        return StringUtils.removeEnd(path.replaceAll("[/]+","/"),"/");
    }

    public static OracleHomes getHomes() {
        return oracleHomes;
    }
}
