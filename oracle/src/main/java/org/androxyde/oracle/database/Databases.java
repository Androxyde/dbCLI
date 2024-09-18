package org.androxyde.oracle.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Databases extends HashSet<Database> {

    Map<String, Database> index = new HashMap<>();

    @Override
    public boolean add(Database d) {
        index.put(d.getOracleSid(), d);
        return super.add(d);
    }

    public Database get(String oracleSid) {
        return index.get(oracleSid);
    }

    public void query() {

    }

}
