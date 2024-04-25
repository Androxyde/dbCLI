package org.androxyde.oracle;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

@Serdeable
@Data
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
