package org.androxyde.oracle.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OracleProcesses {

    @Builder.Default
    Set<OracleProcess> agents = new HashSet<>();

    @Builder.Default
    Set<OracleProcess> databases = new HashSet<>();

    @Builder.Default
    Set<OracleProcess> listeners = new HashSet<>();

    @Builder.Default
    Set<OracleProcess> oms = new HashSet<>();

}
