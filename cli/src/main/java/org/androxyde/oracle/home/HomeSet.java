package org.androxyde.oracle.home;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HomeSet extends HashSet<Home> {

    Map<String, Home> index = new HashMap<>();

    @Override
    public boolean add(Home h) {
        index.put(h.homeLocation, h);
        return super.add(h);
    }

    public Home get(String location) {
        return index.get(location);
    }

}
