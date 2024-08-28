package org.androxyde.os;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.HashSet;
import java.util.Set;

@Builder
@Data
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserInfos {

    Long id;
    String name;
    GroupInfos primaryGroup;

    @Builder.Default
    Set<GroupInfos> groups = new HashSet<>();

}
