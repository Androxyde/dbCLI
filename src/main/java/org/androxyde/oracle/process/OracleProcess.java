package org.androxyde.oracle.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OracleProcess {

    String owner;

    Long pid;

    @JsonIgnore
    String type;

    String name;

    String homeLocation;

    String tnsAdminLocation;

}
