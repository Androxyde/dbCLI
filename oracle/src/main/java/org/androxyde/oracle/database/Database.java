package org.androxyde.oracle.database;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Serdeable
@Data
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Database {

    String owner;
    String homeLocation;
    String homeDbs;
    String tnsAdminLocation;

    String oracleSid;
    String pmonStatus;

    String startMode;
    String openMode;
    String dbId;
    String role;
    
}
