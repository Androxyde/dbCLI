package org.androxyde.oracle.process;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Builder.Default
    Set<OracleProcess> crs = new HashSet<>();

    public Set<OracleProcess> all() {
        return Stream.of(agents,databases,listeners,oms,crs)
                .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<String> homes() {
        return Stream.of(agents,databases,listeners,oms,crs)
                .flatMap(Set::stream).map(p -> p.homeLocation).collect(Collectors.toSet());
    }

}
