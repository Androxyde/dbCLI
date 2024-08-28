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

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof OracleProcess))
            return false;
        OracleProcess other = (OracleProcess)o;
        return other.getPid().equals(pid);
    }

    @Override
    public int hashCode() {
        return pid.hashCode();
    }

}