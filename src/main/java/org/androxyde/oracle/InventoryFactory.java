package org.androxyde.oracle;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public class InventoryFactory {
    public static CentralInventory get(String invPtr) {
        Properties p = new Properties();
        try {
            p.load(new FileReader(new File(invPtr)));
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
        }
        File inventory = new File(p.getProperty("inventory_loc")+"/ContentsXML/inventory.xml");
        XmlMapper xmlMapper = new XmlMapper();
        try {
            return xmlMapper.readValue(inventory, CentralInventory.class);
        } catch (IOException ioe) {
            log.error(ioe.getMessage());
            return null;
        }
    }

}
