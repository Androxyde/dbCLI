package org.androxyde.oracle.inventory;

import org.androxyde.oracle.home.Homes;
import org.androxyde.os.OS;

import java.util.*;

public class CentralInventories {

    private static Map<String, CentralInventory> inventories = new HashMap<>();

    private static Set<String> gchomes = new HashSet<>();

    public static void load(Boolean force) {
        if (inventories.size()==0 || force) {
            inventories.clear();
            CentralInventories.add(OS.toProperties("/etc/oraInst.loc"));
        }
    }

    public static void add(Properties invPtrLoc) {

        if (invPtrLoc.containsKey("inventory_loc")) {
            String inventoryPath = Homes.sanitize(invPtrLoc.getProperty("inventory_loc"));
            if (!inventories.containsKey(invPtrLoc.getProperty("inventory_loc"))) {
                inventories.put(inventoryPath, new CentralInventory(invPtrLoc));
            }
        }

    }

    public static Collection<CentralInventory> get() {
        return inventories.values();
    }

}
