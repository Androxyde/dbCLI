package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import java.util.HashSet;
import java.util.Set;

@Serdeable
@Builder
@Jacksonized
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OracleHomes {

    @Builder.Default
    Set<RDBMSHome> rdbmsHomes = new HashSet<>();

    @Builder.Default
    Set<AGENTHome> agentHomes = new HashSet<>();

    @Builder.Default
    Set<CRSHome> crsHomes = new HashSet<>();

    @Builder.Default
    Set<OMSHome> omsHomes = new HashSet<>();

}
