package org.androxyde.oracle.home;


import java.util.*;

public class Homes {

    private static Set<String> processed = new HashSet<>();
    private static final Map<String, Set<Home>> oracleHomes = new HashMap<>();

    public static void add(Home h) {
        if (processed.add(h.getHomeLocation())) {
            h.fetchLocalInventory();
                if (!oracleHomes.containsKey(h.getHomeType()))
                    oracleHomes.put(h.getHomeType(),new HashSet<>());
                oracleHomes.get(h.getHomeType()).add(h);
        }
    }

    public static Map<String, Set<Home>> get() {
        return oracleHomes;
    }

    public static String sanitize(String path) {
        while (path.endsWith("/")) {
            return sanitize(path.substring(0, path.length() - 1));
        }
        return path.replaceAll("/+","/");
    }

}
