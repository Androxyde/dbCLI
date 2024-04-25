package org.androxyde.oracle;

import org.apache.commons.lang3.StringUtils;

public class Home {

    public static String sanitize(String path) {
        return StringUtils.removeEnd(path.replaceAll("[/]+","/"),"/");
    }

}
