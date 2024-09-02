package org.androxyde.oracle.process;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Builder
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OracleProcesses {

    @JsonIgnore
    @Builder.Default
    Map<Long, OracleProcess> index = new HashMap<>();

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

    public void add(String type, OracleProcess p) {
        switch (type) {
            case "agent" : agents.add(p);
            break;
            case "database" : databases.add(p);
            break;
            case "listener" : listeners.add(p);
            break;
            case "oms" : oms.add(p);
            break;
            case "crs" : crs.add(p);
            break;
        }
        index.put(p.getPid(), p);
    }

    public Set<OracleProcess> all() {
        return Stream.of(agents,databases,listeners,oms,crs)
                .flatMap(Set::stream).collect(Collectors.toSet());
    }

    public Set<String> homes() {
        return Stream.of(agents,databases,listeners,oms,crs)
                .flatMap(Set::stream).map(p -> p.homeLocation).collect(Collectors.toSet());
    }

}
