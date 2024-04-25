package org.androxyde.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
@Builder
@Slf4j
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CommandResult {

    Integer returnCode;
    Boolean stdOutComplete=false;

    @Builder.Default
    List<String> stdout = new LinkedList<>();

    public void addStdout(String line) {
        log.info(line);
        stdout.add(line);
    }

}
