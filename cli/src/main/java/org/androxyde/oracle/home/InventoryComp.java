package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@JacksonXmlRootElement(localName = "COMP")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InventoryComp {

    @JacksonXmlProperty(localName="NAME")
    String name;

    @JacksonXmlProperty(localName="VER")
    String baseVersion;

}
