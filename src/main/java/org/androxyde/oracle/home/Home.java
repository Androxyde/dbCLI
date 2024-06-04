package org.androxyde.oracle.home;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.androxyde.oracle.inventory.InventoryHome;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Serdeable
@Builder
@Jacksonized
@Data
public class Home {

    String homeLocation;
    String homeType;
    String homeRelease;

    public void fetchLocalInventory() {
        File inventory = new File(homeLocation+"/inventory/ContentsXML/comps.xml");
        XmlMapper xm = new XmlMapper();
        XMLInputFactory xif = XMLInputFactory.newInstance();
        Map<String, InventoryComp> comps = new HashMap<>();
        try {
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(inventory));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("COMP".equals(xr.getLocalName())) {
                        InventoryComp comp = xm.readValue(xr, InventoryComp.class);
                        if (List.of("oracle.server","oracle.crs","oracle.sysman.top.agent","oracle.sysman.top.oms","oracle.client").contains(comp.getName())) {
                            comps.put(comp.getName(),comp);
                        }
                    }
                }
            }
            xr.close();
        } catch (Exception e) {
        }
        if (comps.containsKey("oracle.crs")) {
            homeType="CRS";
            homeRelease=comps.get("oracle.crs").getBaseVersion();
        }
        else if (comps.containsKey("oracle.server")) {
            homeType="RDBMS";
            homeRelease=comps.get("oracle.server").getBaseVersion();
        }
        else if (comps.containsKey("oracle.sysman.top.oms")) {
            homeType="OMS";
            homeRelease=comps.get("oracle.sysman.top.oms").getBaseVersion();
        }
        else if (comps.containsKey("oracle.sysman.top.agent")) {
            homeType="AGENT";
            homeRelease=comps.get("oracle.sysman.top.agent").getBaseVersion();
        }
        else if (comps.containsKey("oracle.client")) {
            homeType="CLIENT";
            homeRelease=comps.get("oracle.client").getBaseVersion();
        }
    }
}
