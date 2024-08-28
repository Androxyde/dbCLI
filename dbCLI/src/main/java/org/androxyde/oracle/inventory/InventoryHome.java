package org.androxyde.oracle.inventory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@JacksonXmlRootElement(localName = "HOME")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InventoryHome {

    @JacksonXmlProperty(localName="NAME")
    String name;

    @JacksonXmlProperty(localName="LOC")
    String location;

    @JacksonXmlProperty(localName="REMOVED")
    String removed;

}
