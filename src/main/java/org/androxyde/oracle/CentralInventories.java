package org.androxyde.oracle;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CentralInventories {

    private static Map<String, CentralInventory> inventories = new HashMap<>();

    public static void add(String invPtrLoc) {

        String ptrPath=Home.sanitize(invPtrLoc);

        if (!inventories.containsKey(invPtrLoc)) {
            inventories.put(ptrPath,new CentralInventory(ptrPath));
        }

    }

    public static Collection<CentralInventory> get() {
        return inventories.values();
    }

}
