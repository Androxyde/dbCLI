package org.androxyde.oracle.home;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import org.androxyde.utils.OS;
import org.androxyde.utils.UserInfos;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.Properties;

@Builder
@Data
@Jacksonized
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RDBMSHome {

    String homeLocation;
    String homeRelease;
    String homeDbs;
    String homeOwner;
    String ownerPrimaryGroup;
    String homeInstallGroup;
    String inventoryLocation;

    public void fetchOwner() {
        try {
            UserPrincipal p = Files.getOwner(new File(homeLocation + "/bin/oracle").toPath());
            homeOwner=p.getName();
            UserInfos user = OS.getUserInfos(homeOwner);
            ownerPrimaryGroup=user.getPrimaryGroup().getName();
            Properties prop = new Properties();
            prop.load(new FileReader(new File(homeLocation+"/oraInst.loc")));
            homeInstallGroup=prop.getProperty("inst_group");
            inventoryLocation=prop.getProperty("inventory_loc");
        } catch (Exception e) {}
    }

    public void fetchHomeDbs() {
        try {

        } catch (Exception e) {
            homeDbs=homeLocation+"/dbs";
        }
    }
}
