package org.androxyde.oracle.inventory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.OraUtils;
import org.androxyde.oracle.home.Homes;
import org.androxyde.oracle.process.OracleProcess;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Slf4j
@JacksonXmlRootElement(localName = "INVENTORY")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CentralInventory {

    List<InventoryHome> homes = new LinkedList<>();

    String inventoryLoc;
    String instGroup;

    @JsonIgnore
    Boolean isValid = Boolean.TRUE;

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof CentralInventory))
            return false;
        CentralInventory other = (CentralInventory)o;
        return (other.getInventoryLoc().equals(inventoryLoc) && other.getInstGroup().equals(instGroup));
    }

    @Override
    public int hashCode() {

        return Objects.hash(inventoryLoc, instGroup);

    }

    public CentralInventory(Properties invPtr) {

        if (invPtr.containsKey("inventory_loc") && invPtr.containsKey("inst_group")) {
            try {
                inventoryLoc = OraUtils.sanitize(invPtr.getProperty("inventory_loc"));
                instGroup = invPtr.getProperty("inst_group");
                File inventory = new File(inventoryLoc + "/ContentsXML/inventory.xml");
                XmlMapper xm = new XmlMapper();
                XMLInputFactory xif = XMLInputFactory.newInstance();
                XMLStreamReader xr = xif.createXMLStreamReader(new FileInputStream(inventory));
                while (xr.hasNext()) {
                    xr.next();
                    if (xr.getEventType() == START_ELEMENT) {
                        if ("HOME".equals(xr.getLocalName())) {
                            InventoryHome ihome = xm.readValue(xr, InventoryHome.class);
                            homes.add(ihome);
                        }
                    }
                }
            } catch (IOException ioe) {
                log.error("CENTRALINVENTORY IOERROR : " + ioe.getMessage());
                isValid = Boolean.FALSE;
            } catch (XMLStreamException xmle) {
                log.error("CENTRALINVENTORY XMLERROR : " + xmle.getMessage());
                isValid = Boolean.FALSE;
            }
        } else {
            isValid = Boolean.FALSE;
        }
    }

    public Boolean valid() {
        return isValid;
    }
}
