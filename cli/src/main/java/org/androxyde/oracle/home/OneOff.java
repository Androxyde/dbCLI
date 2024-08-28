package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.androxyde.oracle.process.OracleProcess;

import java.util.Comparator;
import java.util.Set;

@JacksonXmlRootElement(localName = "ONEOFF")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Builder
@Jacksonized
public class OneOff implements Comparable<OneOff> {

    @JacksonXmlProperty(localName="REF_ID")
    Long id;

    @JacksonXmlProperty(localName="UNIQ_ID")
    Long uniqueId;

    @JacksonXmlProperty(localName="TYPE")
    String type;

    @JacksonXmlProperty(localName="DESC")
    String description;

    @JacksonXmlElementWrapper(localName="BUG_LIST")
    private Set<String> bugs;


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof OneOff))
            return false;
        OneOff other = (OneOff) o;
        return other.getId().equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(OneOff o) {
        return this.getId().compareTo(o.getId());
    }
}
