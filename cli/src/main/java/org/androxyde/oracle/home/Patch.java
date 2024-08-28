package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@JacksonXmlRootElement(localName = "patch")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Patch {

    @JacksonXmlProperty(localName="patch-id")
    Long id;

    @JacksonXmlProperty(localName="status")
    String status;

}
