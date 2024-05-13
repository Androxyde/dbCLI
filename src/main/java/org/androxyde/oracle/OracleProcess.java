package org.androxyde.oracle;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Serdeable
public class OracleProcess {
    String owner;
    Long pid;
    String type;
    String name;
    String homeLocation;
    String tnsAdminLocation;
}
