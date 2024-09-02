package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Slf4j
public class Homes {

    @JsonIgnore
    Map<String, Home> index = new HashMap<>();

    Set<Home> rdbmsHomes = new HashSet<>();

    Set<Home> crsHomes = new HashSet<>();

    Set<Home> agentHomes = new HashSet<>();

    public boolean add(Home h) {
        index.put(h.homeLocation, h);
        if (h.getHomeType().equals("RDBMS"))
            return rdbmsHomes.add(h);
        if (h.getHomeType().equals("AGENT"))
            return agentHomes.add(h);
        if (h.getHomeType().equals("CRS"))
            return crsHomes.add(h);

        return false;
    }

    @JsonIgnore
    public Home get(String location) {
        return index.get(location);
    }

}
