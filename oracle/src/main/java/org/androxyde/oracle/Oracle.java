package org.androxyde.oracle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.androxyde.oracle.database.Database;
import org.androxyde.oracle.database.DatabaseSet;
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

    OracleProcesses processes;
    Set<OratabEntry> oratabEntries;
    Set<CentralInventory> inventories;
    Set<GCHome> oragchomelist;
    DatabaseSet databases;
    Homes oracleHomes=new Homes();

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

    public void computeHomes(Homes reference) {

        Set<Home> homes = new HashSet<>();

        ExecutorService pool = Executors.newFixedThreadPool(3);

        try {
            for (GCHome home: Optional.ofNullable(oragchomelist).orElse(new HashSet<>())) {
                if (reference.getIndex().containsKey(home.getHomeLocation())) {
                    homes.add(reference.getIndex().get(home.getHomeLocation()));
                }
                else {
                    Home h = Home.builder().homeLocation(home.getHomeLocation()).build();
                    if (homes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (String home: Optional.ofNullable(processes.homes()).orElse(new HashSet<>())) {
                if (reference.getIndex().containsKey(home)) {
                    homes.add(reference.getIndex().get(home));
                }
                else {
                    Home h = Home.builder().homeLocation(home).build();
                    if (homes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (OratabEntry entry:Optional.ofNullable(oratabEntries).orElse(new HashSet<>())) {
                if (reference.getIndex().containsKey(entry.getHomeLocation())) {
                    homes.add(reference.getIndex().get(entry.getHomeLocation()));
                }
                else {
                    Home h = Home.builder().homeLocation(entry.getHomeLocation()).build();
                    if (homes.add(h))
                        processHome(h, pool);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (CentralInventory entry:Optional.ofNullable(inventories).orElse(new HashSet<>())) {
                for (InventoryHome home:entry.getHomes()) {
                    if (reference.getIndex().containsKey(home.getLocation())) {
                        homes.add(reference.getIndex().get(home.getLocation()));
                    }
                    else {
                        Home h = Home.builder().homeLocation(home.getLocation()).build();
                        if (homes.add(h))
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

        for (Home h:homes) {
            if (h.valid()) {
                oracleHomes.add(h);
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

        if (oracleHomes == null) computeHomes(null);

        databases=new DatabaseSet();

        try {
            Home h = null;
            for (OratabEntry entry:Optional.ofNullable(oratabEntries).orElse(new HashSet<>())) {
                h=oracleHomes.get(entry.getHomeLocation());
                if (h!=null) {
                    Database d = Database.builder().oracleSid(entry.getOracleSid())
                            .homeLocation(entry.getHomeLocation())
                            .pmonStatus("STOPPED").build();
                    databases.add(d);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        try {
            for (OracleProcess p: Optional.ofNullable(processes.getDatabases()).orElse(new HashSet<>())) {
                Database d = databases.get(p.getName());
                if (d==null) {
                    Home h=oracleHomes.get(p.getHomeLocation());
                    if (h!=null) {
                        d = Database.builder().oracleSid(p.getName())
                                .homeLocation(p.getHomeLocation())
                                .pmonStatus("RUNNING").build();
                        databases.add(d);
                    }
                }
                else {
                    d.setPmonStatus("RUNNING");
                    d.setHomeLocation(p.getHomeLocation());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    synchronized public OracleProcesses fetchProcesses() {
        while (locked);
        return processes;
    }

    synchronized public Set<OratabEntry> fetchOratab() {
        while (locked);
        return oratabEntries;
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
            oratabEntries = OraUtils.getOratab("/etc/oratab");

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
            oratabEntries = OraUtils.getOratab("/etc/oratab");

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