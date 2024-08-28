package org.androxyde.refresh.v1.controlers;

import io.micronaut.http.annotation.*;
import org.androxyde.oracle.Oracle;
import org.androxyde.oracle.oratab.OratabEntry;
import org.androxyde.oracle.home.HomeSet;
import org.androxyde.oracle.process.OracleProcesses;
import org.androxyde.os.OS;
import org.androxyde.os.UserInfos;

import java.util.Map;
import java.util.Set;

@Controller("/os")
public class OSController {

    Oracle oracle;

    public OSController() {
        this.oracle=Oracle.builder().build();
    }

    @Get(uri="/users/{name}", produces="application/json")
    public UserInfos get(String name) {

        return OS.getUserInfos(name);

    }

    @Get(uri="/processes", produces="application/json")
    public OracleProcesses getProcesses() {

        return oracle.getProcesses();

    }

    @Get(uri="/oratab", produces="application/json")
    public Set<OratabEntry> getOratab() {

        return oracle.getOratabEntries();

    }

    @Get(uri="/homes", produces="application/json")
    public Map<String, HomeSet> getHomes() {

        return oracle.getOracleHomes();

    }

    @Get(uri="/currentuser", produces="text/plain")
    public String getCurrentUser() {

        return OS.currentUser();

    }

}