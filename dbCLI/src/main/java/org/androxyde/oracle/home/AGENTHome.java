package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Builder
@Data
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AGENTHome {

    String homeLocation;

    String homeRelease;

}
