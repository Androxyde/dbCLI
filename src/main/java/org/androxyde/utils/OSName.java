package org.androxyde.utils;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;

@Serdeable
@Data
@Builder
public class OSName {

    @Builder.Default
    String hostname="localhost";

    @Builder.Default
    String domainname="localdomain";
}
