package org.androxyde.oracle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.database.Database;
import org.androxyde.oracle.database.Databases;
import org.androxyde.oracle.gchomes.GCHome;
import org.androxyde.oracle.home.Home;
import org.androxyde.oracle.home.Homes;
import org.androxyde.oracle.inventory.CentralInventory;
import org.androxyde.oracle.inventory.InventoryHome;
import org.androxyde.oracle.oratab.OratabEntry;
import org.androxyde.oracle.process.OracleProcess;
import org.androxyde.oracle.process.OracleProcesses;
import org.androxyde.os.OS;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Data
@Serdeable
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Slf4j
public class Oracle {

    @JsonIgnore
    Boolean withoratab;

    @JsonIgnore
    Boolean withproceses;

    @JsonIgnore
    Boolean withcentralinventory;

    @JsonIgnore
    Boolean withtools;

    @JsonIgnore
    Boolean withgchomes;

    @JsonIgnore
    Boolean locked=false;

    @JsonIgnore
    Map<String, Database> databaseIndexOnSid = new HashMap<>();

    String defaultTnsAdmin="/apps/oracle/adm/network";
    OracleProcesses processes;
    Set<OratabEntry> oratab;
    Set<CentralInventory> inventories;
    Set<GCHome> oragchomelist;
    Homes homes;
    Set<Database> databases;


    public Oracle(Boolean withoratab, Boolean withprocesses, Boolean withcentralinventory, Boolean withtools, Boolean withgchomes) {

        this.withoratab=withoratab;
        this.withproceses=withprocesses;
        this.withcentralinventory=withcentralinventory;
        this.withtools=withtools;
        this.withgchomes=withgchomes;

        refresh();

    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        Boolean withoratab = Boolean.FALSE;
        Boolean withproceses = Boolean.FALSE;
        Boolean withcentralinventory = Boolean.FALSE;
        Boolean withtools = Boolean.FALSE;
        Boolean withgchomes = Boolean.FALSE;

        public Builder() {
        }

        public Builder withoratab(Boolean withoratab) {
            this.withoratab = withoratab;
            return this;
        }

        public Builder withgchomes(Boolean withgchomes) {
            this.withgchomes = withgchomes;
            return this;
        }

        public Builder withprocesses(Boolean withprocesses) {
            this.withproceses = withprocesses;
            return this;
        }

        public Builder withtools(Boolean withtools) {
            this.withtools = withtools;
            return this;
        }

        public Builder withcentralinventory(Boolean withcentralinventory) {
            this.withcentralinventory=withcentralinventory;
            return this;
        }

        public Oracle build() {
            return new Oracle(withoratab, withproceses, withcentralinventory, withtools, withgchomes);
        }

    }

    public void computeHomes() {

        Set<Home> newhomes = new HashSet<>();

        if (homes==null) homes=new Homes();

        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            for (GCHome home: Optional.ofNullable(oragchomelist).orElse(new HashSet<>())) {
                if (homes.getIndex().containsKey(home.getHomeLocation())) {
                    newhomes.add(homes.getIndex().get(home.getHomeLocation()));
                }
                else {
                    Home h = Home.builder().homeLocation(home.getHomeLocation()).build();
                    if (newhomes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (String home: Optional.ofNullable(processes.homes()).orElse(new HashSet<>())) {
                if (homes.getIndex().containsKey(home)) {
                    newhomes.add(homes.getIndex().get(home));
                }
                else {
                    Home h = Home.builder().homeLocation(home).build();
                    if (newhomes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (OratabEntry entry:Optional.ofNullable(oratab).orElse(new HashSet<>())) {
                if (homes.getIndex().containsKey(entry.getHomeLocation())) {
                    newhomes.add(homes.getIndex().get(entry.getHomeLocation()));
                }
                else {
                    Home h = Home.builder().homeLocation(entry.getHomeLocation()).build();
                    if (newhomes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (CentralInventory entry:Optional.ofNullable(inventories).orElse(new HashSet<>())) {
                for (InventoryHome home:entry.getHomes()) {
                    if (homes.getIndex().containsKey(home.getLocation())) {
                        newhomes.add(homes.getIndex().get(home.getLocation()));
                    }
                    else {
                        Home h = Home.builder().homeLocation(home.getLocation()).build();
                        if (newhomes.add(h))
                            processHome(h, pool);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        pool.shutdown();

        try {
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            pool.shutdownNow();
        }

        homes.clear();

        for (Home h:newhomes) {
            if (h.valid()) {
                homes.add(h);
            }
        }

    }

    public void processHome(Home h, ExecutorService pool) {
        Runnable r = () -> {
            if (Home.tools.containsKey(h.getHomeLocation())) {
                h.fetchToolsDetails();
            }
            else {
                h.fetchLocalInventory();
                if (h.valid()) {
                    h.fetchOwner();
                    h.fetchOracleBaseConfig();
                }
            }
        };

        pool.submit(r);

    }

    public void computeDatabases() {

        if (homes.getIndex().size() == 0) computeHomes();

        databases=new Databases();

        try {
            Home h = null;
            for (OratabEntry entry:Optional.ofNullable(oratab).orElse(new HashSet<>())) {
                h=homes.get(entry.getHomeLocation());
                if (h!=null) {
                    Database d = Database.builder().oracleSid(entry.getOracleSid())
                            .homeLocation(entry.getHomeLocation())
                            .tnsAdminLocation(defaultTnsAdmin)
                            .owner(h.getHomeOwner())
                            .homeDbs(h.getHomeDbs())
                            .pmonStatus("STOPPED").build();
                    databases.add(d);
                    databaseIndexOnSid.put(d.getOracleSid(),d);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (OracleProcess p: Optional.ofNullable(processes.getDatabases()).orElse(new HashSet<>())) {
                Database d = fetchDatabaseFromSid(p.getName());
                Home h=homes.get(p.getHomeLocation());
                if (d==null) {
                    if (h!=null) {
                        d = Database.builder().oracleSid(p.getName())
                                .homeLocation(p.getHomeLocation())
                                .tnsAdminLocation(p.getTnsAdminLocation())
                                .owner(p.getOwner())
                                .homeDbs(h.getHomeDbs())
                                .pmonStatus("RUNNING").build();
                        databases.add(d);
                        databaseIndexOnSid.put(d.getOracleSid(),d);
                    }
                }
                else {
                    d.setPmonStatus("RUNNING");
                    d.setHomeLocation(p.getHomeLocation());
                    d.setTnsAdminLocation(Optional.ofNullable(p.getTnsAdminLocation()).orElse(defaultTnsAdmin));
                    d.setHomeDbs(h.getHomeDbs());
                    d.setOwner(p.getOwner());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    public Database fetchDatabaseFromSid(String sid) {
        return databaseIndexOnSid.get(sid);
    }

    synchronized public OracleProcesses fetchProcesses() {
        while (locked);
        return processes;
    }

    synchronized public Set<OratabEntry> fetchOratab() {
        while (locked);
        return oratab;
    }
    synchronized public void refreshProcesses() {

        while (locked);

        locked = true;

        if (withproceses) {
            if (processes==null)
                processes=OraUtils.getOracleProcesses(null);
            else
                processes = OraUtils.getOracleProcesses(processes);
        }

        locked = false;

    }

    synchronized public void refreshOratab() {

        while (locked);

        locked = true;

        if (withoratab)
            oratab = OraUtils.getOratab("/etc/oratab");

        locked = false;

    }

    synchronized public void refresh() {

        while (locked);

        locked = true;

        if (withproceses) {
            if (processes==null)
                processes=OraUtils.getOracleProcesses(null);
            else
                processes = OraUtils.getOracleProcesses(processes);
        }

        if (withoratab)
            oratab = OraUtils.getOratab("/etc/oratab");

        if (withcentralinventory) {
            inventories = new HashSet<>();
            Properties p = OS.toProperties("/etc/oraInst.loc");
            inventories.add(new CentralInventory(p));
            inventories.removeIf(element -> !element.valid());
        }

        if (withgchomes) {
            try {
                oragchomelist=new HashSet<>();
                for (String line : Files.readAllLines(Path.of("/etc/oragchomelist"))) {
                    String[] entries = line.split(":");
                    if (entries.length==2) {
                        oragchomelist.add(GCHome.builder().homeLocation(entries[0].trim()).homeStateDir(entries[1].trim()).build());
                    }
                }
            } catch (IOException ignored) {}
        }

        locked=false;

    }

}