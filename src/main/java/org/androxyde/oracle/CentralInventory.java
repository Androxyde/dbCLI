package org.androxyde.oracle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Slf4j
@JacksonXmlRootElement(localName = "INVENTORY")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
public class CentralInventory {

    @JacksonXmlProperty(localName="HOME_LIST")
    List<InventoryHome> homes;

}
