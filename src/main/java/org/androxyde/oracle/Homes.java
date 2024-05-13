package org.androxyde.oracle;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Homes {

    private static Map<String, Home> homes = new HashMap<>();

    static void add(String path) {
        if (!homes.containsKey(path)) {
            homes.put(path, new Home());
        }
    }

    public static String sanitize(String path) {
        return StringUtils.removeEnd(path.replaceAll("[/]+","/"),"/");
    }

}
