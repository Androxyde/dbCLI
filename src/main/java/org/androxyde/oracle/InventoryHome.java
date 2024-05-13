package org.androxyde.oracle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@JacksonXmlRootElement(localName = "HOME")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
public class InventoryHome {

    @JacksonXmlProperty(localName="NAME")
    String name;

    @JacksonXmlProperty(localName="LOC")
    String location;

    @JacksonXmlProperty(localName="TYPE")
    String type;

    @JacksonXmlProperty(localName="IDX")
    String index;

    @JacksonXmlProperty(localName="REMOVED")
    String removed;

}
