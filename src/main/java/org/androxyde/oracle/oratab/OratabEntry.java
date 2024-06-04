package org.androxyde.oracle.oratab;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Serdeable
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OratabEntry {

    String oracleSid;
    String homeLocation;
    String autoStart;
    String comment;

    public OratabEntry(String oracleSid, String homeLocation, String autoStart, String comment) {
        this.oracleSid=oracleSid;
        this.homeLocation=homeLocation;
        this.autoStart=autoStart;
        this.comment=comment;
    }
}
