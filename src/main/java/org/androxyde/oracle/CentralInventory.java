package org.androxyde.oracle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Slf4j
@JacksonXmlRootElement(localName = "INVENTORY")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
public class CentralInventory {

    List<InventoryHome> homes = new LinkedList<>();

    String inventoryLoc;
    String instGroup;

    public CentralInventory(String invPtr) {

        Properties p = new Properties();

        try {
            p.load(new FileReader(new File(invPtr)));
            inventoryLoc=p.getProperty("inventory_loc");
            instGroup=p.getProperty("inst_group");
            File inventory = new File(inventoryLoc+"/ContentsXML/inventory.xml");
            XmlMapper xm = new XmlMapper();
            XMLInputFactory xif = XMLInputFactory.newInstance();
            XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(inventory));
            while (xr.hasNext()) {
                xr.next();
                if (xr.getEventType() == START_ELEMENT) {
                    if ("HOME".equals(xr.getLocalName())) {
                        InventoryHome ihome = xm.readValue(xr, InventoryHome.class);
                        homes.add(ihome);
                        Homes.add(ihome.getLocation());
                    }
                }
            }
        } catch (IOException ioe) {
            log.error("CENTRALINVENTORY IOERROR : "+ioe.getMessage());
        } catch (XMLStreamException xmle) {
            log.error("CENTRALINVENTORY XMLERROR : "+xmle.getMessage());
        }
    }

}
