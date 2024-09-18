package org.androxyde.refresh.v1.controlers;

import io.micronaut.http.annotation.*;
import org.androxyde.oracle.Oracle;
import org.androxyde.oracle.database.Database;
import org.androxyde.oracle.database.Databases;
import org.androxyde.oracle.home.Homes;
import org.androxyde.oracle.oratab.OratabEntry;
import org.androxyde.oracle.process.OracleProcesses;
import org.androxyde.os.OS;
import org.androxyde.os.UserInfos;

import java.util.Set;

@Controller("/os")
public class OSController {

    Oracle oracle;

    public OSController() {
        oracle=Oracle.builder().withgchomes(true).withcentralinventory(true).withoratab(true).withprocesses(true).build();;
    }

    @Get(uri="/users/{name}", produces="application/json")
    public UserInfos get(String name) {

        return OS.getUserInfos(name);

    }

    @Get(uri="/processes", produces="application/json")
    public OracleProcesses getProcesses() {

        oracle.refreshProcesses();
        return oracle.fetchProcesses();

    }

    @Get(uri="/oratab", produces="application/json")
    public Set<OratabEntry> getOratab() {

        oracle.refreshOratab();
        return oracle.fetchOratab();

    }

    @Get(uri="/homes", produces="application/json")
    public Homes getHomes() {

        oracle.refresh();
        oracle.computeHomes();
        return oracle.getHomes();

    }

    @Get(uri="/databases", produces="application/json")
    public Set<Database> getDatabases() {

        oracle.refresh();
        oracle.computeHomes();
        oracle.computeDatabases();
        return oracle.getDatabases();

    }

    @Get(uri="/currentuser", produces="text/plain")
    public String getCurrentUser() {

        return OS.currentUser();

    }

}