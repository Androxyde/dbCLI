package org.androxyde.oracle.inventory;

import org.androxyde.oracle.home.Homes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CentralInventories {

    private static Map<String, CentralInventory> inventories = new HashMap<>();

    public static void add(String invPtrLoc) {

        String ptrPath= Homes.sanitize(invPtrLoc);

        if (!inventories.containsKey(invPtrLoc)) {
            inventories.put(ptrPath,new CentralInventory(ptrPath));
        }

    }

    public static Collection<CentralInventory> get() {
        return inventories.values();
    }

}
